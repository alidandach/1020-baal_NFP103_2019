package client;

import java.net.Socket;

public class Client {
    private Socket socket;
    private volatile boolean shutdown;
    private KeyBoardInput keyBoardInput;
    private NetworkInput networkInput;

    public Client() {
        keyBoardInput = new KeyBoardInput("client keyboard", this);
        keyBoardInput.start();
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket s) {
        socket=s;
        networkInput = new NetworkInput("client network", this);
        networkInput.start();
    }

    public boolean isShutdown() {
        return shutdown;
    }

    public void setShutdown() {
        shutdown = true;
    }

    public String toString() {
        return "client " + socket.getLocalAddress() + " on port (" + socket.getLocalPort() + ")";
    }
}
