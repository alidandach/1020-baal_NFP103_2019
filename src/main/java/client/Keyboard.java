package client;


import command.Command;
import context.Banner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import validation.Validation;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.regex.Matcher;


public class Keyboard extends Thread {

    private final static Logger logger = LogManager.getLogger(Keyboard.class);
    private User user;
    private Scanner input;
    private String prefix;

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

   /* private void errorMessage(String message) {
        System.out.println("invalid input...");
        System.out.println(message);
        Banner.adjustHelpMessage("client");
    }

    public synchronized void unplug() {
        input.close();
    }*/

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
                            if (command.length == 2) {
                                //check if user already connected to server
                                if (!user.isConnected()) {
                                    try {
                                        Matcher matcher = Validation.CONNECT.getPattern().matcher(command[1]);
                                        if (matcher.matches()) {
                                            //String host = command[1].substring(0, command[1].indexOf('@'));
                                            String ip = command[1].substring(command[1].indexOf('@') + 1, command[1].indexOf(':'));
                                            String port = command[1].substring(command[1].indexOf(':') + 1);
                                            user.clearBridge();
                                            user.getBridge().put(Command.CONNECT.getCommand());
                                            user.setSocket(new Socket(ip, Integer.parseInt(port)));
                                            logger.info("connected on " + ip + ":" + port);
                                        } else
                                            errorMessage();
                                    } catch (IOException e) {
                                        logger.error("IO exception in keyboard thread\t----->\t" + e.getMessage());
                                    }
                                } else
                                    System.out.println("you are already connected...");
                            } else
                                errorMessage();
                            startPrefix();
                            break;
                        case CLIENTS:
                            if (user.isConnected())
                                user.getBridge().put(Command.CLIENTS.getCommand());
                            else {
                                System.out.println("you are not connected to any server.please use " + Command.CONNECT.getCommand() + " command to connect to server.");
                                startPrefix();
                            }
                            break;
                        case QUIT:
                            user.shutdown(true);
                            break;
                        case HELP:
                            helpMessage();
                            break;
                        default:
                            errorMessage();
                            startPrefix();
                    }
                } catch (InterruptedException e) {
                    logger.error("Interrupted exception in keyboard thread\t----->\t" + e.getMessage());
                    startPrefix();
                }
            } else {
                errorMessage();
                startPrefix();
            }


        }
    }
}
