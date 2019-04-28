package server;

import command.Command;
import context.Banner;

import java.util.Scanner;

public class KeyBoardInput extends Thread {
    private Server server;
    private Scanner input;
    public static String prefix="irc > ";


    public KeyBoardInput(String n, Server s) {
        super(n);
        server = s;
        input = new Scanner(System.in);
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    @Override
    public void run() {
        //display message at startup of program
        Banner.loadBanner();
        Banner.adjustHelpMessage("server");
        System.out.print("irc > ");


        String[] command = null;
        Command cmd = null;


        while (server.isRunning()) {


            command = input.nextLine().split(" ");
            cmd = Command.getCommand(command[0]);

            if (cmd != null) {
                switch (cmd) {
                    case START:
                        if (command.length == 2)
                            System.out.println(server.setPort(command[1]));
                        else
                            System.out.println("invalid input... \n please enter an integer between 1024> and <65536");
                        System.out.print(prefix);
                        break;

                    case WHO:
                        System.out.println(server.listAllClients());
                        System.out.print(prefix);
                        break;

                    case KILL:
                        server.removeClient(null);
                        System.out.print(prefix);
                        break;

                    case SHUTDOWN:
                        server.shutdown();
                        break;

                    case HELP:
                        Banner.adjustHelpMessage("server");
                        System.out.print(prefix);
                        break;
                    default:
                        System.out.println("invalid input...");
                        Banner.adjustHelpMessage("server");
                        System.out.print(prefix);

                }
            } else {
                System.out.println("invalid input...");
                Banner.adjustHelpMessage("server");
                System.out.print(prefix);
            }

        }
    }
}
