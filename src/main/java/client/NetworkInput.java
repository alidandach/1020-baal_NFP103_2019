package client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class NetworkInput extends Thread {
    private Client client;

    public NetworkInput(String n, Client c) {
        super(n);
        client = c;
    }

    public Client getClient() {
        return client;
    }

    @Override
    public void run() {
        Socket socket =null;
        Scanner networkInput=null;
        PrintWriter networkOutput=null;
        String message, response;
        try {
            socket = client.getSocket();
            networkInput = new Scanner(socket.getInputStream());
            networkOutput = new PrintWriter(socket.getOutputStream(), true);
            message = "connect";
            networkOutput.println(message);

            while(!client.isShutdown()) {
                response = networkInput.nextLine();
                System.out.println(response);
            }

        } catch (IOException e) {
            System.out.println("problem with server connection:" + e.getMessage());
            System.exit(-1);
        } finally {
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

