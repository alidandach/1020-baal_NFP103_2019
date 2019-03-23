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
    private Vector<OnlineClient> clients;
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

    public synchronized void addClient(OnlineClient c) {
        c.start();
        clients.add(c);
    }

    public void disconnectAllClients() {
        Iterator<OnlineClient> i = clients.iterator();
        while (i.hasNext())
            i.next().disconnect();
    }

    public synchronized void removeClient(String hostname) {
        Iterator<OnlineClient> i = clients.iterator();

        OnlineClient c = null;
        while (i.hasNext()) {
            c = i.next();
            if (c.getHostName().equals(hostname))
                break;
        }
        if (c != null) {
            c.disconnect();
            c.interrupt();
            clients.remove(c);
        }
    }

    public synchronized String listAllClients() {
        if (clients.size() == 0)
            return "sorry no client connected to this server";
        String out = "hostname\t\t\taddress\t\t\t\tport\n";
        out += "---------------\t\t---------------\t\t------\n";
        for (OnlineClient client : clients)
            out += client.toString();
        return out;
    }
}
