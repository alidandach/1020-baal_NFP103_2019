package server;

import command.Command;
import flags.Echo;
import flags.Identity;
import security.Symmetric;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Base64;

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
                            //positive ack for user
                            output.println(Echo.POSITIVE_ACK.getValue());

                            //Wait for the user to dial the public key
                            request = input.readLine();

                            //send public key to user
                            if (request.trim().equals(Echo.ECHO_CLIENT.getValue())) {
                                String publicKey = Base64.getEncoder().encodeToString(server.getPublicKey().getEncoded());
                                output.println(publicKey);

                                //wait secret key from client
                                request = input.readLine();
                                byte[] decryptedString = server.decrypt(Base64.getDecoder().decode(request.trim().getBytes()));

                                //create new client
                                Client c = new Client(server, socket, decryptedString);

                                //send authentication success
                                String s=Base64.getEncoder().encodeToString(Symmetric.encrypt(Echo.POSITIVE_ACK.getValue().getBytes(), Base64.getEncoder().encodeToString(decryptedString), new byte[16]));
                                output.println(s);

                                //add new client to server
                                server.addClient(c);

                                //Send the identity to the client
                                c.send(Identity.ID.getValue()+c.getId());

                                //Tell the administrator to enter a new client
                                System.out.println("\nnew user is connected.check it by typing " + Command.CLIENTS.getCommand() + " command");
                                System.out.print("irc > ");


                                socket = null;
                            }

                        } else
                            output.println(Echo.NEGATIVE_ACK.getValue());
                    } else
                        output.println("sorry only " + Command.CONNECT.getCommand() + " and " + Command.CLIENTS.getCommand() + "commands working....");
            }
        } catch (IOException e) {
            System.out.println("problem in network thread:" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
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
