package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Stream;


public class Server {
    private int port;
    private volatile boolean running;
    private KeyBoardInput keyBoardInput;
    private NetworkInput networkInput;
    private ServerSocket serverSocket;
    private Vector<Client> clients;
    private Vector<Group> groups;
    private BlockingQueue<String> queue;


    public Server() {
        running = true;
        clients = new Vector<>();
        groups = new Vector<>();
        queue = new ArrayBlockingQueue<>(1);
        keyBoardInput = new KeyBoardInput("server keyboard", this);
        keyBoardInput.start();
    }

    /**
     * method used to set port to start listing
     *
     * @param p int port number
     * @return String message
     */
    String setPort(String p) {
        if (p == null)
            return "null";
        else if (serverSocket != null)
            return "null";
        try {
            int port = Integer.parseInt(p);
            if (port < 1024 || port > 65536)
                return "null";
            this.port = port;
            try {
                serverSocket = new ServerSocket(port);
                networkInput = new NetworkInput("networkInput server", this);
                networkInput.start();
            } catch (IOException e) {
                e.printStackTrace();
                return e.getMessage();
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return "server started...";
    }

    BlockingQueue<String> getQueue() {
        return queue;
    }

    /**
     * method used to check server status
     *
     * @return boolean if true
     */
    synchronized boolean isRunning() {
        return running;
    }

    /**
     * method used to check the status of network on the server
     *
     * @return boolean if true network is started
     */
    synchronized boolean isStarted() {
        return networkInput != null;
    }

    /**
     * this method used to shutdown network thread if it's running and the keyboard thread.
     * after shutdown the network and keyboard threads the main thread turn off automatically
     */
    synchronized void shutdown() {
        try {
            running = false;

            if (networkInput != null) {
                disconnectAllClients();
                PrintWriter output = new PrintWriter(new Socket(InetAddress.getLocalHost(), port).getOutputStream(), true);
                output.println("");
                output.close();
            }

        } catch (IOException e) {
            System.out.println("problem when shutdown the server:" + e.getMessage());
        }

    }

    synchronized ServerSocket getServerSocket() {
        return serverSocket;
    }

    /**
     * method used to add new user
     *
     * @param c Client to be added
     */
    synchronized void addClient(Client c) {
        for (Client client : clients)
            client.send("\nserver say:new user online!");

        clients.add(c);
    }

    /**
     * remove specific user.you need to identify if to need remove theme using keyboard or network
     *
     * @param c            Client to be deleted
     * @param fromKeyboard boolean if true disconnect using keyboard
     */
    synchronized void removeClient(Client c, boolean fromKeyboard) {
        if (fromKeyboard)
            c.disconnectFromKeyboard();
        c.disconnect();
        clients.remove(c);
        broadcast("(pc" + c.getId() + ")" + c.getHostName() + " back offline.");
    }

    /**
     * method used to disconnect all clients connected to server
     * it's used when server need to reset or shutdown
     */
    private void disconnectAllClients() {
        for (Client client : clients) client.disconnect();
        clients = new Vector<>();
    }

    /**
     * method used to get number of clients connected
     *
     * @return int number of clients connected
     */
    synchronized int getNumberOfClients() {
        return clients.size();
    }

    /**
     * method used to get specific user using id
     *
     * @param id int id of user
     * @return Client founded or null if not found
     */
    synchronized Client getClientById(int id) {
        for (Client client : clients)
            if (client.getId() == id)
                return client;
        return null;
    }

    /**
     * method list all clients connected to the server
     *
     * @return String contain all clients with styling
     */
    synchronized String displayClients() {
        if (clients.size() == 0)
            return "sorry no user connected to this server";

        StringBuilder out = new StringBuilder();
        StringBuilder separate = new StringBuilder();

        out.append("\nonline clients\n");
        Stream.generate(() -> "=")
                .limit(15)
                .forEach(separate::append);

        out.append(String.format("%-23s", separate.toString())).append("\n").append("\n");

        separate.setLength(0);


        out.append(String.format("%-15s%-35s%-35s%-15s", "id", "hostname", "address", "port")).append("\n");

        out.append(insertHeaderStyle());


        for (Client client : clients)
            out.append(client.toString()).append("\n");


        return out.toString();
    }

    /**
     * fill header style
     *
     * @return String header
     */
    private String insertHeaderStyle() {
        StringBuilder out = new StringBuilder();
        StringBuilder separate = new StringBuilder();

        Stream.generate(() -> "-")
                .limit(5)
                .forEach(separate::append);
        out.append(String.format("%-15s", separate.toString()));
        separate.setLength(0);

        Stream.generate(() -> "-")
                .limit(15)
                .forEach(separate::append);
        out.append(String.format("%-35s", separate.toString()));
        separate.setLength(0);

        Stream.generate(() -> "-")
                .limit(15)
                .forEach(separate::append);
        out.append(String.format("%-35s", separate.toString()));
        separate.setLength(0);

        Stream.generate(() -> "-")
                .limit(5)
                .forEach(separate::append);
        out.append(separate.toString()).append("\n");
        separate.setLength(0);
        return out.toString();
    }

    /**
     * method used to broadcast message foreach user connected to the server
     *
     * @param message String contain of message
     */
    private void broadcast(String message) {
        for (Client client : clients) {
            client.send("\nserver say:" + message);
        }
    }

    /**
     * method to display all groups
     *
     * @return String contain all groups
     */
    String displayGroups(Client beneficiary) {
        if (groups.size() == 0)
            return "sorry no group created on this server";

        StringBuilder out = new StringBuilder();
        StringBuilder separate = new StringBuilder();

        out.append("\ngroups\n");
        Stream.generate(() -> "=")
                .limit(15)
                .forEach(separate::append);

        out.append(String.format("%-23s", separate.toString())).append("\n").append("\n");

        separate.setLength(0);


        out.append(String.format("%-15s%-35s%-35s%-15s", "id", "group name", "administrator", "joined")).append("\n");
        out.append(insertHeaderStyle());

        for (Group group : groups)
            out.append(group.toString(beneficiary)).append("\n");

        return out.toString();
    }
}
