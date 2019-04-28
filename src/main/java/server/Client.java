package server;

import command.Command;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Client implements Comparable<Client> {
    private static int counter = 1;
    private int id;
    private Server server;
    private Socket socket;
    private Thread transmitter;
    private Thread receiver;
    private volatile boolean connected;
    private BlockingQueue<String> bridge;
    public static String prefix = "irc >";

    private final static Logger logger = LogManager.getLogger(Client.class);

    public Client(Server s, Socket socket) {
        id = counter++;
        server = s;
        this.socket = socket;
        connected = true;
        bridge = new ArrayBlockingQueue<>(1);

        //initialize transmitter
        transmitter = new Thread(() -> {
            PrintWriter output = null;
            try {
                output = new PrintWriter(socket.getOutputStream(), true);
                String data = null;
                while (connected) {
                    //wait to consume message
                    data = bridge.take();

                    //send to client
                    output.println(data);

                    //check if need to turn off this thread
                    if (data.equals(Command.QUIT.getcommand()))
                        break;
                }
            } catch (IOException e) {
                logger.error("IO exception   ----->   " + e.getMessage());
            } catch (InterruptedException e) {
                logger.error("Thread Exception    ----->    " + e.getMessage());
            } finally {
                //logger.info("closing connection.....");
                if (output != null)
                    output.close();
            }
        });
        transmitter.start();

        //initialize receiver
        receiver = new Thread(() -> {
            BufferedReader input = null;
            try {
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String request;
                while (connected) {
                    // receive the command from client
                    request = input.readLine();

                    //parse command
                    Command cmd = Command.getCommand(request);

                    //handle command
                    switch (cmd) {
                        case WHO:
                            bridge.put(server.listAllClients());
                            break;
                        case QUIT:
                            disconnect();
                            break;
                    }
                }
            } catch (IOException e) {
                logger.error("IO exception in thread client\t----->\t" + e.getMessage());
            } catch (InterruptedException e) {
                logger.error("Interrupted exception in thread client\t----->\t" + e.getMessage());
            } finally {
              /*  try {
                    //logger.info("closing connection.....");
                    if (input != null)
                        input.close();
                } catch (IOException e) {
                    logger.error("IO exception in thread client\t----->\t" + e.getMessage());
                }*/
            }

        });
        receiver.start();
    }

    public int getId() {
        return id;
    }

    public String getHostName() {
        return socket.getLocalAddress().getHostName();
    }

    public void disconnect() {
        try {
            connected = false;
            bridge.put(Command.QUIT.getcommand());
            server.removeClient(this);

            System.out.println("\nThere is a client left");
            System.out.print(prefix);


        } catch (InterruptedException e) {
            logger.error("Interrupted exception in thread client\t----->\t" + e.getMessage());
        }
    }


    public void send(String answer) {
        bridge.add(answer);
    }

    private String giveMeMoreSpace(int wordLength, int offset) {
        String out = "";
        for (int i = 0; i < offset - wordLength; i++)
            out += " ";
        return out;
    }

    public String toString() {
        String out = "";
        //id
        out += "" + id;
        out += giveMeMoreSpace(("" + id).length(), 5);
        out += "\t\t\t";

        //host name
        out += socket.getLocalAddress().getHostName();
        out += giveMeMoreSpace(socket.getLocalAddress().getHostName().length(), 15);
        out += "\t\t\t";

        //ip address
        out += socket.getLocalAddress().getHostAddress();
        out += giveMeMoreSpace(socket.getLocalAddress().getHostAddress().length(), 15);
        out += "\t\t\t";

        //port number
        out += socket.getPort();

        return out;
    }

    @Override
    public int compareTo(Client o) {
        if (id > o.getId())
            return 1;
        if (id < o.getId())
            return -1;
        return 0;
    }

    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (!Client.class.isAssignableFrom(obj.getClass()))
            return false;

        final Client c = (Client) obj;

        if (id != c.getId())
            return false;
        return true;
    }
}
