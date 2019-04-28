package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
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
            running = false;
            disconnectAllClients();
            PrintWriter output = new PrintWriter(new Socket(InetAddress.getLocalHost(), port).getOutputStream(), true);
            output.println("");
            output.close();

        } catch (IOException e) {
            System.out.println("problem when shutdown the server:" + e.getMessage());
        }

    }

    public synchronized ServerSocket getServerSocket() {
        return serverSocket;
    }

    public synchronized void addClient(Client c) {
        for (Client client : clients)
            client.send("\nserver say:new client online!");

        clients.add(c);
    }

    public synchronized void removeClient(Client c) {
        clients.remove(c);
        broadcast(c.getHostName() + " left from server.");
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

        String out = "\n";

        out += "online clients\n";
        out += insertSeparator('=', "online clients".length());
        out += "\n";
        out += "\n";

        out += "id";
        out += giveMeMoreSpace("id".length(), 5);
        out += "\t\t\t";

        out += "hostname";
        out += giveMeMoreSpace("hostname".length(), 15);
        out += "\t\t\t";

        out += "address";
        out += giveMeMoreSpace("address".length(), 15);
        out += "\t\t\t";

        out += "port";
        out += giveMeMoreSpace("port".length(), 5);

        out += "\n";

        out += insertSeparator('-', 5);
        out += "\t\t\t";

        out += insertSeparator('-', 15);
        out += "\t\t\t";


        out += insertSeparator('-', 15);
        out += "\t\t\t";

        out += insertSeparator('-', 5);
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

    private String insertSeparator(char character, int number) {
        String out = "";
        for (int i = 0; i < number; i++)
            out += character;
        return out;
    }

    public void broadcast(String message) {
        for (Client client : clients) {
            client.send("\nserver say:" + message);
        }
    }
}
