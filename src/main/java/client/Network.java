package client;


import command.Command;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;

public class Network {
    private User user;
    private Thread transmitter;
    private Thread receiver;
    private volatile boolean connected;
    private final static Logger logger = LogManager.getLogger(Network.class);

    public Network(User u) {
        user = u;
        connected = true;

        //initialize transmitter
        transmitter = new Thread(() -> {
            Socket socket = null;
            PrintWriter output = null;
            try {
                socket = user.getSocket();
                output = new PrintWriter(socket.getOutputStream(), true);
                String request;
                while (connected) {
                    //wait to consume message
                    request = user.getBridge().take();

                    //send to server
                    output.println(request);

                    //check if need to turn off this thread
                    if (request.equals(Command.QUIT.getcommand()))
                        break;

                }

            } catch (IOException e) {
                logger.error("IO exception in transmitter thread\t----->\t" + e.getMessage());
            } catch (InterruptedException e) {
                logger.error("Interrupted exception in transmitter thread\t----->\t" + e.getMessage());
            } finally {
                System.out.println("closing transmitter connection.....");
                /*if (output != null)
                    output.close();*/
            }


        });
        transmitter.start();

        //initialize receiver
        receiver = new Thread(() -> {
            Socket socket = null;
            BufferedReader input = null;
            try {
                socket = user.getSocket();
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String response = null;
                while (connected) {
                    //read data from server and display data on console
                    int c;

                    // receive the command from server
                    //response = input.readLine();
                    response = "";


                    do {
                        c = socket.getInputStream().read();
                        response += (char) c;
                    } while (socket.getInputStream().available() > 0);


                    //Maybe disconnected from server
                    if (response != null && response.equals(Command.QUIT.getcommand()+"\r\n")) {
                        //disconnect transmitter thread
                        user.getBridge().put(Command.QUIT.getcommand());
                        connected = false;
                        return;
                    }

                    System.out.print(response);

                    //adjusting console
                    System.out.print("irc > ");
                }
            } catch (IOException e) {
                logger.error("IO exception in receiver thread\t----->\t" + e.getMessage());
            } catch (InterruptedException e) {
                logger.error("Interrupted exception in receiver thread\t----->\t" + e.getMessage());
            } finally {
                try {
                    System.out.println("closing receiver connection.....");
                    if (input != null)
                        input.close();
                } catch (IOException e) {
                    logger.error("IO exception in receiver thread\t----->\t" + e.getMessage());
                }
            }

        });
        receiver.start();

        logger.info("socket is plugged");
    }

    public boolean isConnected() {
        return connected;
    }

}

