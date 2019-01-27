package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Vector;


public class Server {
    private int port;
    private volatile boolean shutdown;
    private KeyBoardInput keyBoardInput;
    private NetworkInput networkInput;
    private ServerSocket serverSocket;
    private Vector<OnlineClient> clients;


    public Server() {
        shutdown = false;
        clients = new Vector<>();
        keyBoardInput = new KeyBoardInput("server keyboard", this);
        keyBoardInput.start();
    }

    public int getPort() {
        return port;
    }

    public String setPort(String p) {
        if (p == null)
            return "port is null";
        if (serverSocket != null)
            return "server already start listening on port " + this.port;
        try {
            int port = Integer.parseInt(p);
            if (port < 1024 || port > 65536)
                return "please enter an integer between 1024> and <65536";
            this.port = port;
            try {
                serverSocket = new ServerSocket(this.port, 50, InetAddress.getLocalHost());
                networkInput = new NetworkInput("networkInput server", this);
                networkInput.start();
                return "server has started on port " + this.port;
            } catch (IOException e) {
                return e.getMessage();
            }
        } catch (NumberFormatException e) {
            return "please enter an integer between 1024> and <65536";
        }
    }

    public boolean isShutdown() {
        return shutdown;
    }

    public void setShutdown() {
        shutdown = true;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public synchronized void addClient(OnlineClient c) {
        clients.add(c);
    }

    public synchronized void removeClient(OnlineClient c) {
        clients.remove(c);
    }

    public synchronized String listAllClients() {
        if (clients.size() == 0)
            return "sorry no client connected to this server";
        String out = "hostname \t\t address \t\t port \n";
        out += "--------- \t\t --------------- \t ------ \n";
        for (OnlineClient client : clients)
            out += client.toString();
        return out;
    }
}
