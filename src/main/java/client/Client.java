package client;

import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Client {
    private Socket socket;
    private volatile boolean running;
    private KeyBoard keyBoard;
    private Network network;
    private BlockingQueue<String> bridge;

    public Client() {
        bridge = new ArrayBlockingQueue<>(1);
        keyBoard = new KeyBoard("client keyboard", this);
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
        network.interrupt();
        keyBoard.interrupt();
        running = false;
    }

    public String toString() {
        return "client " + socket.getLocalAddress() + " on port (" + socket.getLocalPort() + ")";
    }
}
