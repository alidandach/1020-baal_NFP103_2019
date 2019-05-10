package server;

import command.Command;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import validation.Validation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Matcher;

public class Client implements Comparable<Client> {
    private static int counter = 1;
    private int id;
    private Server server;
    private Socket socket;
    private volatile boolean connected;
    private volatile boolean disconnectFromKeyboard;
    private BlockingQueue<String> bridge;

    private final static Logger logger = LogManager.getLogger(Client.class);

    Client(Server s, Socket socket) {
        id = counter++;
        server = s;
        this.socket = socket;
        connected = true;
        bridge = new ArrayBlockingQueue<>(1);

        //initialize transmitter
        //wait to consume message
        //send to client
        //check if need to turn off this thread
        Thread transmitter = new Thread(() -> {
            PrintWriter output;
            try {
                output = new PrintWriter(socket.getOutputStream(), true);
                String data;
                while (connected) {
                    //wait to consume message
                    data = bridge.take();

                    //send to client
                    output.println(data);

                    //check if need to turn off this thread
                    if (data.equals(Command.QUIT.getCommand()))
                        break;
                }
            } catch (IOException e) {
                Interrupt();
                newLine();
                logger.error("IO exception\t----->\t" + e.getMessage());
            } catch (InterruptedException e) {
                Interrupt();
                newLine();
                logger.error("Thread Exception\t----->\t" + e.getMessage());
            }
        });
        transmitter.start();

        //initialize receiver
        // receive the command from client
        //parse command
        //handle command
        Thread receiver = new Thread(() -> {
            BufferedReader input = null;
            try {
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String request;
                while (connected) {
                    // receive the command from client
                    request = input.readLine();

                    //request contains command and parameters
                    String [] command=request.trim().split(" ");

                    //parse command
                    Command cmd = Command.getCommand(command[0]);

                    //handle command
                    if (cmd != null) {
                        switch (cmd) {
                            case CLIENTS:
                                bridge.put(server.listAllClients());
                                break;
                            case QUIT:
                                server.removeClient(this, false);

                                if (!disconnectFromKeyboard) {
                                    System.out.println("\nThere is a client left");
                                    startPrefix();
                                }

                                break;
                            case CHAT_WITH_USER:
                                if(command.length==3){
                                    //parse id of pc
                                    Matcher matcher = Validation.CLIENT.getPattern().matcher(command[1]);
                                    if (matcher.matches()) {
                                        String[] pcs = command[1].split("pc");
                                        Client c = server.getClientById(Integer.parseInt(pcs[1]));
                                        if(c!=null)
                                            c.send("(pc"+id+")"+getHostName()+" say:"+command[2]);

                                    }
                                }
                                break;
                        }
                    }

                }
            } catch (IOException e) {
                Interrupt();
                newLine();
                logger.error("IO exception in thread client\t----->\t" + e.getMessage());
                startPrefix();
            } catch (InterruptedException e) {
                Interrupt();
                newLine();
                logger.error("Interrupted exception in thread client\t----->\t" + e.getMessage());
                startPrefix();
            } finally {
                try {
                    if (input != null)
                        input.close();
                } catch (IOException e) {
                    logger.error("IO exception in thread client\t----->\t" + e.getMessage());
                    startPrefix();
                }
            }

        });
        receiver.start();
    }

    /**
     * method return id of current client
     * @return int id of client
     */
    int getId() {
        return id;
    }

    /**
     * method used to adjusting console style
     */
    private void startPrefix() {
        String prefix = "irc >";
        System.out.print(prefix);
    }

    /**
     * method used to put new line on console server
     */
    private void newLine() {
        System.out.println();
    }

    /**
     * method to return host name of current client
     * @return String indicate the host name of client
     */
    String getHostName() {
        return socket.getLocalAddress().getHostName();
    }

    /**
     * this method used to disconnect the current client
     */
    void disconnect() {
        try {
            connected = false;
            bridge.put(Command.QUIT.getCommand());

        } catch (InterruptedException e) {
            logger.error("Interrupted exception in thread client\t----->\t" + e.getMessage());
        }
    }

    /**
     * this method used by the admin of server to kill specific client
     */
    void disconnectFromKeyboard() {
        disconnectFromKeyboard = true;
    }


    /**
     * every user connected have thread.this method used to kill current client(2 thread in and out) and remove it from clients list on the server
     */
    private void Interrupt() {
        bridge.clear();
        server.removeClient(this, false);
    }

    /**
     * this method used to send message (answer) to the user
     * @param answer String to be send towards user
     */
    void send(String answer) {
        bridge.add(answer);
    }


    public String toString() {
        return String.format("%-15s%-35s%-35s%-15s", id, getHostName(), socket.getLocalAddress().getHostAddress(), socket.getPort());
    }

    @Override
    public int compareTo(Client o) {
        return Integer.compare(id, o.getId());
    }

    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (!Client.class.isAssignableFrom(obj.getClass()))
            return false;

        final Client c = (Client) obj;

        return id == c.getId();
    }
}
