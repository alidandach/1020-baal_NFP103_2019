package user;


import command.Command;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

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
                    output.println(request);

                    //check if need to turn off this thread
                    if (request.equals(Command.QUIT.getCommand()))
                        break;

                }

            } catch (IOException e) {
                logger.error("IO exception in transmitter thread\t----->\t" + e.getMessage());
            } catch (InterruptedException e) {
                logger.error("Interrupted exception in transmitter thread\t----->\t" + e.getMessage());
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


                    //Maybe disconnected from server
                    if (response.toString().equals(Command.QUIT.getCommand() + "\r\n")) {
                        //disconnect transmitter thread
                        user.disconnect();
                        connected = false;
                        return;
                    }

                    //Maybe file in form of byte
                    String[] file = response.toString().split("0xff");
                    if (file.length == 3) {
                        Files.write(Paths.get(file[0] + "." + file[2]), file[1].getBytes());
                        System.out.println("new file received");
                    } else
                        System.out.print(response);

                    //adjusting console
                    startPrefix();

                    //clear response
                    response.setLength(0);
                }
            } catch (IOException e) {
                System.out.println();
                logger.error("IO exception in receiver thread\t----->\t" + e.getMessage());
            } catch (InterruptedException e) {
                System.out.println();
                logger.error("Interrupted exception in receiver thread\t----->\t" + e.getMessage());
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
                    logger.error("IO exception in receiver thread\t----->\t" + e.getMessage());
                } catch (InterruptedException e) {
                    logger.error("Interrupted exception in receiver thread\t----->\t" + e.getMessage());
                }

            }

        });
        receiver.start();

        logger.info("socket is plugged");
    }

    void disconnectFromKeyboard() {
        disconnectFromKeyboard = true;
    }

    private void startPrefix() {
        System.out.print(prefix);
    }

}

