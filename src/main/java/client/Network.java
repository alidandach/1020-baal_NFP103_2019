package client;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;

public class Network extends Thread {
    private Client client;
    private final static Logger logger = LogManager.getLogger(Keyboard.class);

    public Network(String n, Client c) {
        super(n);
        client = c;
        logger.info("network thread created");
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
            logger.error("IO exception   ----->   " + e.getMessage());
        } catch (InterruptedException e) {
            logger.error("Thread Exception    ----->    "+e.getMessage());
        } finally {
            try {
                logger.info("closing connection.....");
                if (socket != null)
                    socket.close();
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException e) {
                logger.error("IO exception   ----->   " + e.getMessage());
            }
        }
    }
}

