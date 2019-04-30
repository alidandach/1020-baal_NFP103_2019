package client;

import command.Command;

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

     synchronized BlockingQueue<String> getBridge() {
        return bridge;
    }

     synchronized Socket getSocket() {
        return socket;
    }

     synchronized void setSocket(Socket s) {
        socket = s;
        network = new Network(this);
    }

     boolean isRunning() {
        return running;
    }

     synchronized void shutdown() throws InterruptedException {
        if(network!=null)
            network.disconnectFromKeyboard();

        running = false;

        if (isConnected())
            disconnect();
    }

    /**
     * this method used to disconnect user in safe mode
     */
     synchronized void disconnect() throws InterruptedException {
        clearBridge();
        bridge.put(Command.QUIT.getCommand());
        network = null;
    }


     synchronized void clearBridge() {
        bridge.clear();
    }

     synchronized boolean isConnected() {
        return network != null;
    }

    public synchronized String toString() {
        return "client " + socket.getLocalAddress() + " on port (" + socket.getLocalPort() + ")";
    }
}
