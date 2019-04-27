package client;

import command.Command;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class User {
    private Socket socket;
    private volatile boolean running;
    private Keyboard keyBoard;
    private Network network;
    private BlockingQueue<String> bridge;

    public User() {
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
        network = new Network(this);
    }

    public boolean isRunning() {
        return running;
    }

    public synchronized void shutdown() throws IOException, InterruptedException {
        running = false;
        bridge.put(Command.QUIT.getcommand());
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