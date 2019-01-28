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
            return "null";
        else if (serverSocket != null)
            return "null";
        if(p!=null && serverSocket==null) {
            try {
                int port = Integer.parseInt(p);
                if (port < 1024 || port > 65536)
                   return "null";
                this.port = port;
                try {
                    serverSocket = new ServerSocket(this.port, 50, InetAddress.getLocalHost());
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
        return "started";
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
