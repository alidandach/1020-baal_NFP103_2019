package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
        StringBuilder response=new StringBuilder();

        try {
            socket = client.getSocket();
            output = new PrintWriter(socket.getOutputStream(), true);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            output.println(client.getBridge().take());

            while (client.isRunning()) {
                //send data to server
                output.println(client.getBridge().take());

                //read data from server
                String line;
                while((line=input.readLine())!=null) {
                    response.append(line);
                    System.out.println(line);
                    response.append(System.getProperty("line.separator"));
                }
                System.out.println("finish");

                //display data on console
                System.out.println(response.toString());

                //adjusting console
                System.out.print("irc > ");

                //cleanup response
                response.setLength(0);
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

