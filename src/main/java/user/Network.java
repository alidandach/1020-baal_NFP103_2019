package user;


import command.Command;
import flags.Identity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import validation.Validation;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.Socket;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.regex.Matcher;

class Network {
    private User user;
    private volatile boolean connected;
    private volatile boolean first;
    private volatile boolean disconnectFromKeyboard;
    private String prefix;
    private final static Logger logger = LogManager.getLogger(Network.class);

    Network(User u) {
        user = u;
        connected = true;
        first = true;
        prefix = "irc > ";

        //initialize transmitter
        //wait to consume message
        //produce to server
        //check if need to turn off this thread
        Thread transmitter = new Thread(() -> {
            Socket socket;
            PrintWriter output;
            try {
                socket = user.getSocket();
                output = new PrintWriter(socket.getOutputStream(), true);
                String request;
                while (connected) {
                    //wait to consume message
                    request = user.consume();

                    //produce to server
                    output.println(Base64.getEncoder().encodeToString(user.encrypt(request.getBytes())));

                    //check if need to turn off this thread
                    if (request.equals(Command.QUIT.getCommand()))
                        break;

                }

            } catch (IOException e) {
                logger.error("io exception in transmitter thread\t----->\t" + e.getMessage());
            } catch (InterruptedException e) {
                logger.error("Interrupted exception in transmitter thread\t----->\t" + e.getMessage());
            } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            } finally {
                if (first)
                    System.out.println();

                if (!disconnectFromKeyboard)
                    System.out.println("closing transmitter connection.....");

                if (!first && !disconnectFromKeyboard) {
                    System.out.println("back offline.try connect to the server");
                    startPrefix();
                }
                first = !first;
            }


        });
        transmitter.start();

        //initialize receiver
        //read data from server and display data on console
        Thread receiver = new Thread(() -> {
            Socket socket;
            BufferedReader input = null;
            try {
                socket = user.getSocket();
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                StringBuilder response;
                while (connected) {
                    //read data from server and display data on console
                    int c;

                    // receive the command from server
                    response = new StringBuilder();


                    do {
                        c = socket.getInputStream().read();
                        response.append((char) c);
                    } while (socket.getInputStream().available() > 0);

                    String dataDecrypted = new String(user.decrypt(Base64.getDecoder().decode(response.toString().trim().getBytes())));

                    //Maybe server send id
                    Matcher matcher = Validation.ID.getPattern().matcher(dataDecrypted.trim());

                    //Maybe server send file in form of byte
                    String[] file = dataDecrypted.trim().split(flags.File.DATA_SEPARATOR.getValue());

                    //Maybe disconnected from server
                    if (dataDecrypted.equals(Command.QUIT.getCommand())) {
                        //disconnect transmitter thread
                        user.disconnect();
                        connected = false;
                        return;
                    } else if (matcher.matches()) {
                        String[] ids = dataDecrypted.trim().split(Identity.ID.getValue());
                        user.setId(Integer.parseInt(ids[1]));
                    } else if (file.length == 5) {

                        File receivedFile = new File("from_" + file[1] + "_id_" + file[2] + "_" + System.currentTimeMillis() + file[4]);

                        if (receivedFile.createNewFile()) {
                            FileOutputStream fos = new FileOutputStream(receivedFile);
                            fos.write(convertToByte(file[3]));
                            fos.flush();
                            fos.close();

                            System.out.println("new file received from " + file[1]);
                            System.out.println("saved to:" + receivedFile.getAbsolutePath());
                        }


                        //adjusting console
                        startPrefix();
                    } else {
                        System.out.println("\n"+dataDecrypted);

                        //adjusting console
                        startPrefix();
                    }


                    //clear response
                    response.setLength(0);
                }
            } catch (IOException | InterruptedException | NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (first)
                        System.out.println();
                    if (!disconnectFromKeyboard)
                        System.out.print("closing receiver connection.....");
                    if (input != null)
                        input.close();
                    user.disconnect();

                    if (!first && !disconnectFromKeyboard) {
                        System.out.println("back offline.try connect to the server");
                        startPrefix();
                    }

                    first = !first;
                } catch (IOException e) {
                    logger.error("io exception in receiver thread\t----->\t" + e.getMessage());
                } catch (InterruptedException e) {
                    logger.error("Interrupted exception in receiver thread\t----->\t" + e.getMessage());
                }

            }

        });
        receiver.start();

        logger.info("socket is plugged on " + user.getSocket().getLocalAddress().getHostAddress() + ":" + user.getSocket().getPort());
    }

    private byte[] convertToByte(String fileData) {
        String s = fileData.substring(1, fileData.length() - 1).trim();
        String[] temp = s.split(",");
        byte[] out = new byte[temp.length];
        for (int i = 0; i < temp.length; i++)
            out[i] = Byte.parseByte(temp[i].trim());
        return out;
    }

    void disconnectFromKeyboard() {
        disconnectFromKeyboard = true;
    }

    private void startPrefix() {
        System.out.print(prefix);
    }

}

