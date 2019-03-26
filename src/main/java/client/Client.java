package client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public void setSocket(String host,int port) throws IOException {
        socket = new Socket(InetAddress.getByName(host),port);
        network = new Network("client network", this);
        network.start();
    }

    public boolean isRunning() {
        return running;
    }

    public void shutdown() throws IOException{
        if (socket != null)
            socket.close();
        if (network != null)
            network.interrupt();
        keyBoard.interrupt();
        running = false;
    }

    public void clearBridge(){
        bridge.clear();
    }

    public boolean isConnected() {
        return network != null;
    }

    public String toString() {
        return "client " + socket.getLocalAddress() + " on port (" + socket.getLocalPort() + ")";
    }
}
