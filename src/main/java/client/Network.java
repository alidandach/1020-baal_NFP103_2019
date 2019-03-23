package client;


import java.io.*;
import java.net.Socket;

public class Network extends Thread {
    private Client client;

    public Network(String n, Client c) {
        super(n);
        client = c;
    }

    public Client getClient() {
        return client;
    }

    @Override
    public void run() {
        Socket socket = null;
        PrintWriter output = null;
        BufferedReader input = null;

        try {
            socket = client.getSocket();
            output = new PrintWriter(socket.getOutputStream(), true);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            output.println(client.getBridge().take());

            while (client.isRunning()) {
                //send data to server
                output.println(client.getBridge().take());

                //read data from server and display data on console
                int c;
                String data = "";
                do {
                    c = socket.getInputStream().read();
                    data+=(char)c;
                } while(socket.getInputStream().available()>0);

                System.out.println(data);

                //adjusting console
                System.out.print("irc > ");
            }

        } catch (IOException e) {
            System.out.println("problem with server connection:" + e.getMessage());
        } catch (InterruptedException e) {
            System.out.println("problem with your queue:" + e.getMessage());
        } finally {
            try {
                System.out.println("\nClosing connection…");
                if (socket != null)
                    socket.close();
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException e) {
                System.out.println("Unable to disconnect!" + e.getMessage());
                System.exit(1);
            }
        }
    }
}

