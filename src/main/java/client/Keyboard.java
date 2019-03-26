package client;


import command.Command;
import context.Banner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.slf4j.LoggerFactory;
import validation.Validation;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;
import java.util.regex.Matcher;

/*import org.apache.log4j.PropertyConfigurator;*/

// Import log4j classes.

public class Keyboard extends Thread {
    private final static Logger logger = LogManager.getLogger(Keyboard.class);

    //PropertyConfigurator.configure(Banner.class.getClassLoader().getResourceAsStream("log4j2.xml"));
    // PropertyConfigurator.configure(Banner.class.getClassLoader().getResourceAsStream("log4j2.properties"));
    // LoggerContext context = (LoggerContext) LogManager.getContext(false);
    // context.setConfigLocation(Thread.currentThread().getContextClassLoader().getResource("log4j2.properties").toURI());

    private Client client;
    private Scanner input;

    public Keyboard(String n, Client c) {
        super(n);
        client = c;
        input = new Scanner(System.in);
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public void run() {
        //display message at startup of program
        Banner.loadBanner();
        Banner.adjustHelpMessage("client");
        System.out.print("irc > ");
        String[] command = null;
        Command cmd = null;

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
                                            String host = command[1].substring(0, command[1].indexOf('@'));
                                            String ip = command[1].substring(command[1].indexOf('@') + 1, command[1].indexOf(':'));
                                            String port = command[1].substring(command[1].indexOf(':') + 1);
                                            client.getBridge().put(Command.CONNECT.getcommand());
                                            client.setSocket(ip, Integer.parseInt(port));
                                            System.out.print("irc > ");
                                        }
                                    } catch (IOException e) {
                                        logger.trace("Hello World");
                                        logger.debug("Hello World");
                                        logger.info("Hello World");
                                        logger.warn("Hello World");
                                        logger.error("Hello World");
                                        System.out.print("irc > ");
                                    }
                                } else {
                                    System.out.println("you are already connected...");
                                    System.out.print("irc > ");
                                }

                            }
                            break;
                        case WHO:
                            if (client.isConnected())
                                client.getBridge().put(Command.WHO.getcommand());
                            else {
                                System.out.println("invalid input...");
                                System.out.println("you are not connected to any server.please use " + Command.CONNECT.getcommand() + " command to connect to server.");
                                Banner.adjustHelpMessage("client");
                                System.out.print("irc > ");
                            }
                            break;
                        case QUIT:
                            try {
                                client.shutdown();
                            } catch (IOException e) {

                            }
                            break;
                        case HELP:
                            Banner.adjustHelpMessage("client");
                            System.out.print("irc > ");
                            break;
                        default:
                            System.out.println("invalid input...");
                            Banner.adjustHelpMessage("client");
                            System.out.print("irc > ");
                    }
                } catch (InterruptedException e) {
                    System.out.println("problem in keyboard and network threads..." + e.getMessage());
                    System.out.print("irc > ");
                }
            } else {
                System.out.println("invalid input...");
                Banner.adjustHelpMessage("client");
                System.out.print("irc > ");
            }

        }
    }
}
