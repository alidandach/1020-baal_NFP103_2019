package client;

import java.io.IOException;
import java.net.*;
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
        keyBoard = new Keyboard("client keyboard thread", this);
        keyBoard.start();
        running = true;
    }

    public synchronized BlockingQueue<String> getBridge() {
        return bridge;
    }

    public synchronized Socket getSocket() {
        return socket;
    }

    public synchronized void setSocket(Socket s) throws IOException {
        socket = s;
        network = new Network("client network thread", this);
        network.start();
    }

    public boolean isRunning() {
        return running;
    }

    public synchronized void shutdown(){
        clearBridge();
        running = false;
        keyBoard.unplug();
    }

    public synchronized void clearBridge(){
        bridge.clear();
    }

    public synchronized boolean isConnected() {
        return network != null;
    }

    public synchronized String toString() {
        return "client " + socket.getLocalAddress() + " on port (" + socket.getLocalPort() + ")";
    }
}
