package client;


import command.Command;
import context.Banner;
import validation.Validation;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;
import java.util.regex.Matcher;

public class Keyboard extends Thread {
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
            command = input.nextLine().split(" ");
            cmd = Command.getCommand(command[0]);

            if (cmd != null) {
                try {
                    switch (cmd) {
                        case CONNECT:
                            if (command.length == 2) {
                                Matcher matcher = Validation.CONNECT.getPattern().matcher(command[1]);
                                if (matcher.matches()) {
                                    String host = command[1].substring(0, command[1].indexOf('@'));
                                    String ip = command[1].substring(command[1].indexOf('@') + 1, command[1].indexOf(':'));
                                    String port = command[1].substring(command[1].indexOf(':') + 1);
                                    client.getBridge().put(Command.CONNECT.getcommand());
                                    client.setSocket(new Socket(InetAddress.getByName(ip), Integer.parseInt(port)));
                                    System.out.print("irc > ");
                                }
                            }
                            break;
                        case WHO:
                            client.getBridge().put(Command.WHO.getcommand());
                            break;
                        case QUIT:
                            client.shutdown();
                            break;
                        case HELP:
                            Banner.adjustHelpMessage("client");
                            break;
                        default:
                            System.out.println("invalid input...");
                            Banner.adjustHelpMessage("client");
                            System.out.print("irc > ");
                    }
                } catch (IOException e) {
                    System.out.println("problem in client socket..." + e.getMessage());
                    System.out.print("irc > ");
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
