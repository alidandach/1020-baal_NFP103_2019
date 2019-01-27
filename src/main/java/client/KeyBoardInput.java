package client;

import command.ClientCommand;
import context.Banner;
import validation.Validation;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;
import java.util.regex.Matcher;

public class KeyBoardInput extends Thread {
    private Client client;
    private Scanner input;

    public KeyBoardInput(String n,Client c) {
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
        ClientCommand cmd = null;

        while (!client.isShutdown()) {
            command = input.nextLine().split(" ");
            cmd = ClientCommand.getCommand(command[0]);

            if (cmd != null) {
                switch (cmd) {
                    case CONNECT:
                        if (command.length == 2) {
                            Matcher matcher = Validation.CONNECT.getPattern().matcher(command[1]);
                            if (matcher.matches()) {
                                String host = command[1].substring(0, command[1].indexOf('@'));
                                String ip = command[1].substring(command[1].indexOf('@') + 1, command[1].indexOf(':'));
                                String port = command[1].substring(command[1].indexOf(':') + 1);
                                System.out.println("command success");
                                try {
                                    client.setSocket(new Socket(InetAddress.getByName(ip),Integer.parseInt(port)));
                                    System.out.println("command success");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        break;
                    case WHO:break;
                    case QUIT:
                        client.setShutdown();
                        break;
                    case HELP:
                        Banner.adjustHelpMessage("client");
                        break;
                    case START:
                        break;
                }
            } else {
                System.out.println("invalid input...");
                Banner.adjustHelpMessage("client");
            }
            System.out.print("irc > ");
        }
    }
}
