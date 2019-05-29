package server;

import command.Command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class NetworkInput extends Thread {
    private Server server;
    private static final String username = "root";
    private static final String password = "root";

    NetworkInput(String n, Server s) {
        super(n);
        server = s;
    }

    @Override
    public void run() {
        Socket socket = null;
        PrintWriter output = null;
        BufferedReader input = null;
        String request;
        try {

            while (server.isRunning()) {
                //waite for new user
                socket = server.getServerSocket().accept();
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                output = new PrintWriter(socket.getOutputStream(), true);
                request = input.readLine();


                String[] command = request.trim().split(" ");

                //parse command
                Command cmd = Command.getCommand(command[0]);

                if (cmd != null)
                    if (cmd == Command.CONNECT) {
                        if (command.length == 3 && command[1].trim().equals(username) && command[2].trim().equals(password)) {
                            Client c = new Client(server, socket);
                            server.addClient(c);
                            c.send("0xee" + c.getId());
                            System.out.println("\nnew user is connected.check it by typing " + Command.CLIENTS.getCommand() + " command");
                            System.out.print("irc > ");
                            socket = null;
                        } else
                            output.println("0x6e65676174697665");
                    } else
                        output.println("sorry only " + Command.CONNECT.getCommand() + " and " + Command.CLIENTS.getCommand() + "commands working....");
            }
        } catch (IOException e) {
            System.out.println("problem in network thread:" + e.getMessage());
        } finally {
            try {
                System.out.println("\nClosing connection…");
                if (socket != null)
                    socket.close();
                if (input != null)
                    input.close();
                if (output != null)
                    output.close();
            } catch (IOException ioEx) {
                System.out.println("Unable to disconnect!");
                System.exit(1);
            }
        }
    }
}
