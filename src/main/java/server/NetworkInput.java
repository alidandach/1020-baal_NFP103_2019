package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class NetworkInput extends Thread {
    private Server server;

    public NetworkInput(String n, Server s) {
        super(n);
        server = s;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }


    @Override
    public void run() {
        Socket socket =null;
        Scanner networkInput=null;
        PrintWriter networkOutput=null;
        String message, response;
        try {
            socket = server.getServerSocket().accept();
            networkInput = new Scanner(socket.getInputStream());
            networkOutput = new PrintWriter(socket.getOutputStream(), true);

            while (!server.isShutdown()) {
                response = networkInput.nextLine();
                //Display server's response to user…
                System.out.println("\nSERVER> " + response);


                switch (response) {
                    case "connect":
                        OnlineClient c = new OnlineClient(socket, "connected !");
                        server.addClient(c);
                        System.out.println("new client is connected.check it by typing who command");
                        System.out.print("irc > ");
                        break;

                    case "who":
                        networkOutput.println(server.listAllClients());
                        break;
                    default:
                        networkOutput.println("sorry only connect and who command working....");
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                System.out.println("\nClosing connection…");
                socket.close();
            } catch (IOException ioEx) {
                System.out.println("Unable to disconnect!");
                System.exit(1);
            }
        }
    }
}
