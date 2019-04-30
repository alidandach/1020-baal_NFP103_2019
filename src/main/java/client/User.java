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

    User() {
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

    public synchronized void setSocket(Socket s) {
        socket = s;
        network = new Network(this);
    }

    public boolean isRunning() {
        return running;
    }

    public synchronized void shutdown(boolean disconnectFromKeyboard) throws InterruptedException {
        if(disconnectFromKeyboard)
            network.disconnectFromKeyboard();

        running = false;

        if (isConnected())
            disconnect();
    }

    /**
     * this method used to disconnect user in safe mode
     */
    public synchronized void disconnect() throws InterruptedException {
        clearBridge();
        bridge.put(Command.QUIT.getCommand());
        network = null;
    }


    public synchronized void clearBridge() {
        bridge.clear();
    }

    public synchronized boolean isConnected() {
        return network != null;
    }

    public synchronized String toString() {
        return "client " + socket.getLocalAddress() + " on port (" + socket.getLocalPort() + ")";
    }
}
