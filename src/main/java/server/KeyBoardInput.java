package server;

import command.Command;
import context.Banner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import validation.Validation;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.util.regex.Matcher;

public class KeyBoardInput extends Thread {
    private final static Logger logger = LogManager.getLogger(KeyBoardInput.class);
    private Server server;
    private Scanner input;


    KeyBoardInput(String n, Server s) {
        super(n);
        server = s;
        input = new Scanner(System.in);
    }

    private void startPrefix() {
        String prefix = "irc > ";
        System.out.print(prefix);
    }


    @Override
    public void run() {
        //display message at startup of program
        Banner.loadBanner();
        Banner.adjustHelpMessage("server");
        startPrefix();


        String[] command;
        Command cmd;


        while (server.isRunning()) {

            command = input.nextLine().trim().split(" ");
            cmd = Command.getCommand(command[0]);

            if (cmd != null) {
                try {
                    switch (cmd) {
                        case START:
                            if (command.length == 2) {
                                Matcher matcherPort = Validation.PORT.getPattern().matcher(command[1]);
                                if (matcherPort.matches()) {
                                    if (server.setPort(command[1])) {
                                        System.out.println("server started.....");
                                        System.out.println("generate public key and private key...");
                                        System.out.println("please wait...");
                                        server.generatePair();
                                        System.out.println("public key:\n" + server.getPublicKey().toString() + "\nprivate key:\n" + server.getPrivateKey().toString());
                                    } else
                                        System.out.println("server already stared.....");
                                } else
                                    System.out.println("invalid input.. please enter an integer between 1024> and <65536");
                            } else
                                System.out.println("invalid input.. please enter an integer between 1024> and <65536");
                            startPrefix();
                            break;

                        case CLIENTS:
                            System.out.println(server.displayClients());
                            startPrefix();
                            break;

                        case KILL:
                            if (server.isNetworkDown())
                                System.out.println("sorry...you must to start listening on a specific port");
                            else if (server.getNumberOfClients() == 0)
                                System.out.println("sorry...No user to kill");
                            else if (command.length != 2)
                                System.out.println("invalid input...check it by typing " + Command.HELP.getCommand() + " command");
                            else if (command[1] != null) {
                                Matcher matcher = Validation.CLIENT.getPattern().matcher(command[1]);
                                if (matcher.matches()) {
                                    String[] s = command[1].split("pc");

                                    Client c = server.getClientById(Integer.parseInt(s[1]));
                                    if (c != null)
                                        server.removeClient(c, true);
                                    else
                                        System.out.println("maybe user left or not exist...check it by typing " + Command.CLIENTS.getCommand());
                                } else
                                    System.out.println("invalid input...check it by typing " + Command.HELP.getCommand() + " command");
                            }
                            startPrefix();
                            break;

                        case LIST_GROUPS:
                            if (server.isNetworkDown())
                                System.out.println("sorry...you must to start listening on a specific port");
                            else
                                System.out.println(server.displayGroups(null));
                            startPrefix();
                            break;
                        case MEMBERS_OF_GROUP:
                            if (server.isNetworkDown())
                                System.out.println("sorry...you must to start listening on a specific port");
                            else {
                                Group group = server.getGroup(command[1]);
                                if (group != null)
                                    System.out.println(group.displayMembers());
                                else
                                    System.out.println("sorry group not found!");
                            }
                            startPrefix();
                            break;

                        case DELETE_GROUP:
                            if (server.isNetworkDown())
                                System.out.println("sorry...you must to start listening on a specific port");
                            else {
                                if (server.removeGroup(null, command[1], true))
                                    System.out.println(command[1] + " deleted");
                                else
                                    System.out.println("sorry group not found!");
                            }
                            startPrefix();
                            break;

                        case PAIR:
                            if (server.isNetworkDown())
                                System.out.println("sorry...you must to start listening on a specific port");
                            else
                                System.out.println("public key:\n" + server.getPublicKey().toString() + "\nprivate key:\n" + server.getPrivateKey().toString());
                            startPrefix();
                            break;

                        case QUIT:
                            server.shutdown();
                            break;

                        case HELP:
                            Banner.adjustHelpMessage("server");
                            startPrefix();
                            break;
                        default:
                            System.out.println("invalid input...");
                            Banner.adjustHelpMessage("server");
                            startPrefix();
                    }
                } catch (IOException e) {
                    System.out.println();
                    logger.error("io exception in keyboard thread\t----->\t" + e.getMessage());
                    startPrefix();
                } catch (NumberFormatException e) {
                    System.out.println();
                    logger.error("Number format exception in keyboard thread\t----->\t" + e.getMessage());
                    startPrefix();
                } catch (NoSuchAlgorithmException e) {
                    logger.error("encryption algorithm asymmetric exception in keyboard thread\t----->\t" + e.getMessage());
                    startPrefix();
                }
            } else {
                System.out.println("invalid input...");
                Banner.adjustHelpMessage("server");
                startPrefix();
            }

        }
    }
}
