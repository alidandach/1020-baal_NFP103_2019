package server;

import command.Command;
import context.Banner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import validation.Validation;

import java.util.Scanner;
import java.util.regex.Matcher;

public class KeyBoardInput extends Thread {
    private Server server;
    private Scanner input;
    private final static Logger logger = LogManager.getLogger(KeyBoardInput.class);


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
                switch (cmd) {
                    case START:
                        if (command.length == 2)
                            System.out.println(server.setPort(command[1]));
                        else
                            System.out.println("invalid input... \n please enter an integer between 1024> and <65536");
                        startPrefix();
                        break;

                    case CLIENTS:
                        System.out.println(server.displayClients());
                        startPrefix();
                        break;

                    case KILL:
                        if (!server.isStarted())
                            System.out.println("sorry...you must to start listening on a specific port");
                        else if (server.getNumberOfClients() == 0)
                            System.out.println("sorry...No user to kill");
                        else if (command.length != 2)
                            System.out.println("invalid input...check it by typing " + Command.HELP.getCommand() + " command");
                        else if (command[1] != null) {
                            Matcher matcher = Validation.CLIENT.getPattern().matcher(command[1]);
                            if (matcher.matches()) {
                                String[] s = command[1].split("pc");
                                try {
                                    Client c = server.getClientById(Integer.parseInt(s[1]));
                                    if (c != null)
                                        server.removeClient(c, true);
                                    else
                                        System.out.println("maybe user left or not exist...check it by typing " + Command.CLIENTS.getCommand());
                                } catch (NumberFormatException e) {
                                    System.out.println();
                                    logger.error("Number format exception in keyboard thread\t----->\t" + e.getMessage());
                                }

                            } else
                                System.out.println("invalid input...check it by typing " + Command.HELP.getCommand() + " command");
                        }
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
            } else {
                System.out.println("invalid input...");
                Banner.adjustHelpMessage("server");
                startPrefix();
            }

        }
    }
}
