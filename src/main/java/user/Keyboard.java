package user;


import command.Command;
import context.Banner;
import flags.Echo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import security.Asymmetric;
import validation.Validation;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Scanner;
import java.util.regex.Matcher;


public class Keyboard extends Thread {
    private User user;
    private Scanner input;
    private String prefix;
    private final static Logger logger = LogManager.getLogger(Keyboard.class);

    Keyboard(String n, User c) {
        super(n);
        user = c;
        input = new Scanner(System.in);
        prefix = "irc > ";
    }

    private void startPrefix() {
        System.out.print(prefix);
    }

    private void helpMessage() {
        Banner.adjustHelpMessage("client");
        startPrefix();
    }

    private void errorMessage() {
        System.out.println("invalid input...");
        Banner.adjustHelpMessage("client");
    }

    @Override
    public void run() {
        //display banner and help message at startup of program
        Banner.loadBanner();
        helpMessage();

        String[] command;
        Command cmd;

        while (user.isRunning()) {
            command = input.nextLine().trim().split(" ");
            cmd = Command.getCommand(command[0]);

            if (cmd != null) {
                try {
                    switch (cmd) {
                        case CONNECT:
                            if (command.length == 7) {
                                //check if user already connected to server
                                if (!user.isConnected()) {

                                    StringBuilder request = new StringBuilder();
                                    for (int i = 1; i < command.length; i++)
                                        request.append(command[i]).append(" ");

                                    Matcher matcher = Validation.CONNECT.getPattern().matcher(request.toString().trim());
                                    if (matcher.matches()) {
                                        String username = command[2];
                                        String password = command[4];

                                        String[] host = command[6].split(":");
                                        String ip = host[0];
                                        String port = host[1];

                                        user.clearBridge();

                                        Socket s = new Socket(ip, Integer.parseInt(port));
                                        PrintWriter out = new PrintWriter(s.getOutputStream(), true);
                                        BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                                        out.println(Command.CONNECT.getCommand() + " " + username + " " + password);
                                        String response = in.readLine();


                                        //authentication check
                                        if (response.trim().equals(Echo.NEGATIVE_ACK.getValue()))
                                            System.out.println("invalid username or password");

                                        else {

                                            //get public key from server {echo client}
                                            out.println(Echo.ECHO_CLIENT.getValue());

                                            //read public key
                                            response = in.readLine();

                                            //set public key
                                            user.setPublicKey(Base64.getDecoder().decode(response.trim().getBytes()));

                                            //generate AES key
                                            user.setSecretKey();
                                            logger.info("secret key generate: " + Base64.getEncoder().encodeToString(user.getSecretKey()));

                                            //send AES key to server
                                            out.println(Base64.getEncoder().encodeToString(Asymmetric.encrypt(user.getPublicKey(), user.getSecretKey())));

                                            //wait accept connection from server
                                            response = in.readLine();

                                            //decrypt data
                                            String data = new String(user.decrypt(Base64.getDecoder().decode(response.trim().getBytes())));

                                            //if data encrypted contains positive ack
                                            if (data.equals(Echo.POSITIVE_ACK.getValue()))
                                                user.setSocket(s);
                                            else
                                                System.out.println("server not accept your key please try again...");
                                        }

                                    } else
                                        errorMessage();

                                } else
                                    System.out.println("you are already connected...");
                            } else
                                errorMessage();
                            startPrefix();
                            break;
                        case CLIENTS:
                            if (user.isConnected())
                                if (command.length != 1) {
                                    System.out.println("invalid input...");
                                    errorMessage();
                                } else
                                    user.produce(Command.CLIENTS.getCommand());
                            else {
                                System.out.println("you are not connected to any server.please use " + Command.CONNECT.getCommand() + " command to connect to server.");
                                startPrefix();
                            }
                            break;
                        case CHAT_WITH_USER:
                            if (user.isConnected()) {
                                if (command.length >= 3) {
                                    //parse id of pc
                                    Matcher matcher = Validation.CLIENT.getPattern().matcher(command[1]);
                                    if (matcher.matches()) {
                                        String[] pcs = command[1].split("pc");
                                        int partnerId = Integer.parseInt(pcs[1]);
                                        if (partnerId != user.getId()) {
                                            String s = Arrays.toString(command);
                                            user.produce(s.substring(1, s.length() - 1).replace(",", ""));
                                        } else {
                                            System.out.println("you talk to yourself");
                                        }
                                    } else
                                        errorMessage();
                                } else
                                    errorMessage();
                            } else
                                System.out.println("you are not connected to any server.please use " + Command.CONNECT.getCommand() + " command to connect to server.");

                            startPrefix();
                            break;
                        case CHAT_ON_GROUP:
                            if (user.isConnected()) {
                                if (command.length >= 3) {
                                    //parse id of pc
                                    Matcher matcher = Validation.GROUP.getPattern().matcher(command[1]);
                                    if (matcher.matches()) {
                                        String s = Arrays.toString(command);
                                        user.produce(s.substring(1, s.length() - 1).replace(",", ""));
                                    } else
                                        errorMessage();
                                } else
                                    errorMessage();
                            } else
                                System.out.println("you are not connected to any server.please use " + Command.CONNECT.getCommand() + " command to connect to server.");

                            startPrefix();
                            break;
                        case CREATE_GROUP:
                            if (user.isConnected()) {
                                if (command.length == 2) {
                                    user.produce(Command.CREATE_GROUP.getCommand() + " " + command[1]);
                                } else {
                                    errorMessage();
                                    startPrefix();
                                }
                            } else {
                                System.out.println("you are not connected to any server.please use " + Command.CONNECT.getCommand() + " command to connect to server.");
                                startPrefix();
                            }

                            break;
                        case JOIN_GROUP:
                            if (user.isConnected()) {
                                if (command.length == 2) {
                                    user.produce(Command.JOIN_GROUP.getCommand() + " " + command[1]);
                                } else
                                    errorMessage();
                            } else {
                                System.out.println("you are not connected to any server.please use " + Command.CONNECT.getCommand() + " command to connect to server.");
                                startPrefix();
                            }

                            break;
                        case EXIT_GROUP:
                            if (user.isConnected()) {
                                if (command.length == 2) {
                                    user.produce(Command.EXIT_GROUP.getCommand() + " " + command[1]);
                                } else {
                                    errorMessage();
                                    startPrefix();
                                }
                            } else {
                                System.out.println("you are not connected to any server.please use " + Command.CONNECT.getCommand() + " command to connect to server.");
                                startPrefix();
                            }

                            break;
                        case DELETE_GROUP:
                            if (user.isConnected()) {
                                if (command.length == 2) {
                                    user.produce(Command.DELETE_GROUP.getCommand() + " " + command[1]);
                                } else {
                                    errorMessage();
                                    startPrefix();
                                }
                            } else {
                                System.out.println("you are not connected to any server.please use " + Command.CONNECT.getCommand() + " command to connect to server.");
                                startPrefix();
                            }

                            break;
                        case LIST_GROUPS:
                            if (user.isConnected()) {
                                if(command.length!=1){
                                    System.out.println("invalid input....");
                                    errorMessage();
                                    startPrefix();
                                }
                                user.produce(Command.LIST_GROUPS.getCommand());
                            } else {
                                System.out.println("you are not connected to any server.please use " + Command.CONNECT.getCommand() + " command to connect to server.");
                                startPrefix();
                            }
                            break;
                        case MEMBERS_OF_GROUP:
                            if (user.isConnected())
                                if (command.length == 2)
                                    user.produce(command[0] + " " + command[1]);
                                else {
                                    errorMessage();
                                    startPrefix();
                                }
                            else {
                                System.out.println("you are not connected to any server.please use " + Command.CONNECT.getCommand() + " command to connect to server.");
                                startPrefix();
                            }
                            break;
                        case SEND_FILE:
                            if (user.isConnected()) {
                                if (command.length == 3) {
                                    //parse id of pc
                                    Matcher matcherUser = Validation.CLIENT.getPattern().matcher(command[1]);
                                    //parse id of group
                                    Matcher matcherGroup = Validation.GROUP.getPattern().matcher(command[1]);

                                    File file = new File(command[2]);
                                    String extension = file.getName().substring(file.getName().lastIndexOf("."));
                                    if (!file.exists())
                                        System.out.println("sorry file not exist!");

                                    else if (matcherUser.matches()) {

                                        String[] pcs = command[1].split("pc");
                                        int partnerId = Integer.parseInt(pcs[1]);
                                        if (partnerId != user.getId()) {
                                            //send file
                                            user.produce(Command.SEND_FILE.getCommand() + " pc" + partnerId + " " + flags.File.DATA_SEPARATOR.getValue() + Arrays.toString(Files.readAllBytes(file.toPath())) + flags.File.DATA_SEPARATOR.getValue() + extension);
                                        } else
                                            System.out.println("impossible to send to yourself the file");


                                    } else if (matcherGroup.matches()) {
                                        String[] groups = command[1].split("grp");
                                        int groupId = Integer.parseInt(groups[1]);
                                        //send file
                                        user.produce(Command.SEND_FILE.getCommand() + " grp" + groupId + " " + flags.File.DATA_SEPARATOR.getValue() + Arrays.toString(Files.readAllBytes(file.toPath())) + flags.File.DATA_SEPARATOR.getValue() + extension);
                                    }


                                } else
                                    errorMessage();
                            } else
                                System.out.println("you are not connected to any server.please use " + Command.CONNECT.getCommand() + " command to connect to server.");
                            startPrefix();
                            break;
                        case QUIT:
                            user.shutdown();
                            break;
                        case HELP:
                            helpMessage();
                            break;
                        case MY_ID:
                            if (user.isConnected())
                                if (command.length != 1) {
                                    System.out.println("invalid input...");
                                    errorMessage();
                                } else
                                    System.out.println("your id:" + user.getId());
                            else
                                System.out.println("you are not connected to any server.please use " + Command.CONNECT.getCommand() + " command to connect to server.");
                            startPrefix();
                            break;
                        default:
                            errorMessage();
                            startPrefix();
                    }
                } catch (InterruptedException e) {
                    logger.error("Interrupted exception in keyboard thread\t----->\t" + e.getMessage());
                    startPrefix();
                } catch (IOException ignored) {
                    System.out.println("Unable to connect to the server maybe it disabled");
                    startPrefix();
                } catch (InvalidKeySpecException e) {
                    logger.error("Invalid key Spec exception in keyboard thread\t----->\t" + e.getMessage());
                    startPrefix();
                } catch (NoSuchAlgorithmException e) {
                    logger.error("No Such Algorithm exception in keyboard thread\t----->\t" + e.getMessage());
                    startPrefix();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                errorMessage();
                startPrefix();
            }


        }
    }
}
