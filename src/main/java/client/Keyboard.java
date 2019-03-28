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
    private Client client;
    private Scanner input;
    private String prefix;

    public Keyboard(String n, Client c) {
        super(n);
        client = c;
        input = new Scanner(System.in);
        prefix = "irc > ";
    }

    private void startPrefix() {
        System.out.print(prefix);
    }

    private void helpMessage(){
        Banner.adjustHelpMessage("client");
        startPrefix();
    }

    private void errorMessage() {
        logger.warn("invalid input...");
        Banner.adjustHelpMessage("client");
    }

    private void errorMessage(String message) {
        logger.warn("invalid input...");
        System.out.println(message);
        Banner.adjustHelpMessage("client");
    }

    public synchronized void unplug(){
        input.close();
    }

    @Override
    public void run() {
        //display banner and help message at startup of program
        Banner.loadBanner();
        helpMessage();

        String[] command;
        Command cmd;

        while (client.isRunning()) {
            command = input.nextLine().trim().split(" ");
            cmd = Command.getCommand(command[0]);

            if (cmd != null) {
                try {
                    switch (cmd) {
                        case CONNECT:
                            if (command.length == 2) {
                                //check if client already connected to server
                                if (!client.isConnected()) {
                                    try {
                                        Matcher matcher = Validation.CONNECT.getPattern().matcher(command[1]);
                                        if (matcher.matches()) {
                                            //String host = command[1].substring(0, command[1].indexOf('@'));
                                            String ip = command[1].substring(command[1].indexOf('@') + 1, command[1].indexOf(':'));
                                            String port = command[1].substring(command[1].indexOf(':') + 1);
                                            client.clearBridge();
                                            client.getBridge().put(Command.CONNECT.getcommand());
                                            client.setSocket(new Socket(ip, Integer.parseInt(port)));
                                            logger.info("connected on " + ip + ":" + port);
                                        } else
                                            errorMessage();
                                    } catch (IOException e) {
                                        logger.error("IO exception   ----->   " + e.getMessage());
                                    }
                                } else
                                    logger.warn("you are already connected...");
                            } else
                                errorMessage();
                            startPrefix();
                            break;
                        case WHO:
                            if (client.isConnected())
                                client.getBridge().put(Command.WHO.getcommand());
                            else
                                errorMessage("you are not connected to any server.please use " + Command.CONNECT.getcommand() + " command to connect to server.");
                            startPrefix();
                            break;
                        case QUIT:
                            client.shutdown();
                            break;
                        case HELP:
                           helpMessage();
                            break;
                        default:
                            errorMessage();
                    }
                } catch (InterruptedException e) {
                    logger.error("exception in Thread -----> " + e.getMessage());
                    startPrefix();
                }
            } else
                errorMessage();


        }
    }
}
