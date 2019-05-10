package client;

import command.Command;

import java.net.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class User {
    private Socket socket;
    private Keyboard keyboard;
    private Network network;
    private BlockingQueue<String> bridge;
    private volatile boolean running;

    User() {
        bridge = new ArrayBlockingQueue<>(1);
        keyboard = new Keyboard("client keyboard thread", this);
        keyboard.start();
        running = true;
    }

    synchronized BlockingQueue<String> getBridge() {
        return bridge;
    }

    /**
     * this method return the plugged socket of the user
     * @return plugged socket
     */
    synchronized Socket getSocket() {
        return socket;
    }

    /**
     * this method used to initialize network thread and put the user online with the server
     * @param s socket plugged into server
     */
    synchronized void setSocket(Socket s) {
        socket = s;
        network = new Network(this);
    }

    /**
     * this method used to check if server is still running
     * @return boolean if true the user is still running else user exactly turned off
     */
    synchronized boolean isRunning() {
        return running;
    }

    /**
     * this method used to shutdown network thread if it's running and the keyboard thread.
     * after shutdown the network and keyboard threads the main thread turn off automatically
     * @throws InterruptedException in case of thread is waiting, sleeping, or otherwise occupied, and the thread is interrupted, either before or during the activity.
     */
    synchronized void shutdown() throws InterruptedException {
        if (network != null)
            network.disconnectFromKeyboard();

        running = false;

        if (isConnected())
            disconnect();

        keyboard = null;
    }

    /**
     * this method used to disconnect user in safe mode
     *
     * @throws InterruptedException in case of thread is waiting, sleeping, or otherwise occupied, and the thread is interrupted, either before or during the activity.
     */
    synchronized void disconnect() throws InterruptedException {
        clearBridge();
        bridge.put(Command.QUIT.getCommand());
        network = null;
    }

    /**
     * this method used clear the all messages between network and keyboard
     *
     */
    synchronized void clearBridge() {
        bridge.clear();
    }

    /**
     * this method used to send message via network to server
     * @param message is the string to be send towards server
     * @throws InterruptedException in case of thread is waiting, sleeping, or otherwise occupied, and the thread is interrupted, either before or during the activity.
     */
    synchronized void send(String message) throws InterruptedException {
        bridge.put(message);
    }

    /**
     * this method used
     * @return boolean if is true the user is connected else is offline
     */
    synchronized boolean isConnected() {
        return network != null;
    }

    public synchronized String toString() {
        return "client " + socket.getLocalAddress() + " on port (" + socket.getLocalPort() + ")";
    }
}
