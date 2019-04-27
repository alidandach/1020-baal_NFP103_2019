package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


public class Server {
    private int port;
    private volatile boolean running;
    private KeyBoardInput keyBoardInput;
    private NetworkInput networkInput;
    private ServerSocket serverSocket;
    private Vector<Client> clients;
    private BlockingQueue<String> queue;


    public Server() {
        running = true;
        clients = new Vector<>();
        queue = new ArrayBlockingQueue<>(1);
        keyBoardInput = new KeyBoardInput("server keyboard", this);
        keyBoardInput.start();
    }

    public int getPort() {
        return port;
    }

    public String setPort(String p) {
        if (p == null)
            return "null";
        else if (serverSocket != null)
            return "null";
        if (p != null && serverSocket == null) {
            try {
                int port = Integer.parseInt(p);
                if (port < 1024 || port > 65536)
                    return "null";
                this.port = port;
                try {
                    serverSocket = new ServerSocket(port, 50, InetAddress.getLocalHost());
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
        }
        return "server started...";
    }

    public BlockingQueue<String> getQueue() {
        return queue;
    }

    public synchronized boolean isRunning() {
        return running;
    }

    public synchronized void shutdown() {
        try {
            disconnectAllClients();
            if (serverSocket != null)
                serverSocket.close();
            if (networkInput != null)
                networkInput.interrupt();
            running = false;
        } catch (IOException e) {
            System.out.println("problem when shutdown the server:" + e.getMessage());
        }

    }

    public synchronized ServerSocket getServerSocket() {
        return serverSocket;
    }

    public synchronized void addClient(Client c) {
        clients.add(c);
    }

    public synchronized void removeClient(Client c) {
        clients.remove(c);
    }

    public void disconnectAllClients() {
        Iterator<Client> i = clients.iterator();
        while (i.hasNext())
            i.next().disconnect();
        clients = new Vector<>();
    }

    public synchronized String listAllClients() {
        if (clients.size() == 0)
            return "sorry no client connected to this server";

        String out = "";

        out += "hostname";
        out += giveMeMoreSpace("hostname".length(), 15);
        out += "\t\t\t";

        out += "address";
        out += giveMeMoreSpace("address".length(), 15);
        out += "\t\t\t";

        out += "port";
        out += giveMeMoreSpace("port".length(), 5);

        out += "\n";

        out += insertDash(15);
        out += "\t\t\t";


        out += insertDash(15);
        out += "\t\t\t";

        out += insertDash(5);
        out += "\n";

        for (Client client : clients) {
            out += client.toString();
            out += "\n";
        }

        return out;
    }

    private String giveMeMoreSpace(int wordLength, int offset) {
        String out = "";
        for (int i = 0; i < offset - wordLength; i++)
            out += " ";
        return out;
    }

    private String insertDash(int number) {
        String out = "";
        for (int i = 0; i < number; i++)
            out += "-";
        return out;
    }

    public void broadcast(String message) {
        for (Client client : clients) {
            client.send(message);
        }
    }
}
