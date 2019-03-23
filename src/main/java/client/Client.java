package client;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Client {
    private Socket socket;
    private volatile boolean running;
    private Keyboard keyBoard;
    private Network network;
    private BlockingQueue<String> bridge;

    public Client() {
        bridge = new ArrayBlockingQueue<>(1);
        keyBoard = new Keyboard("client keyboard", this);
        keyBoard.start();
        running = true;
    }

    public BlockingQueue<String> getBridge() {
        return bridge;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket s) {
        socket = s;
        network = new Network("client network", this);
        network.start();
    }

    public boolean isRunning() {
        return running;
    }

    public void shutdown() {
        try {
            if (socket != null)
                socket.close();
            running = false;
            if (network != null)
                network.interrupt();
            keyBoard.interrupt();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String toString() {
        return "client " + socket.getLocalAddress() + " on port (" + socket.getLocalPort() + ")";
    }
}
