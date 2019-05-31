package user;

import command.Command;
import security.Symmetric;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.net.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class User {
    private int id;
    private Socket socket;
    private Keyboard keyboard;
    private Network network;
    private Symmetric symmetric;
    private PublicKey publicKey;
    private volatile boolean running;
    private BlockingQueue<String> bridge;

    public User() {
        bridge = new ArrayBlockingQueue<>(1);
        keyboard = new Keyboard("user keyboard thread", this);
        keyboard.start();
        running = true;
    }

    /**
     * getter for id
     *
     * @return int id of user
     */
    int getId(){
        return id;
    }

    /**
     * setter for id
     *
     * @param id int
     */
    void setId(int id){
        this.id=id;
    }

    /**
     * getter for secret key between user and server
     *
     * @return bytes secret key
     */
    byte[] getSecretKey(){
        return symmetric.getSecretKey();
    }

    /**
     * generate secret key
     *
     * @throws NoSuchAlgorithmException occur
     */
    void setSecretKey() throws NoSuchAlgorithmException {
        symmetric=new Symmetric();
        symmetric.setKey();
    }

    /**
     * getter for the public key
     *
     * @return String public key
     */
    PublicKey getPublicKey(){
        return publicKey;
    }

    /**
     * set public key of the server
     *
     * @param keyBytes key in form of byte
     */
    void setPublicKey(byte[] keyBytes) throws InvalidKeySpecException, NoSuchAlgorithmException {
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        publicKey=kf.generatePublic(spec);
    }

    byte[] encrypt(byte[] data) throws IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, InvalidAlgorithmParameterException {
        return symmetric.encrypt(data);
    }

    byte[] decrypt(byte[] data) throws IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, InvalidAlgorithmParameterException {
       return symmetric.decrypt(data);
    }

    /**
     * this method return the plugged socket of the user
     *
     * @return plugged socket
     */
    synchronized Socket getSocket() {
        return socket;
    }

    /**
     * this method used to initialize network thread and put the user online with the server
     *
     * @param s socket plugged into server
     */
    synchronized void setSocket(Socket s) {
        socket = s;
        network = new Network(this);
    }

    /**
     * this method used to check if server is still running
     *
     * @return boolean if true the user is still running else user exactly turned off
     */
    synchronized boolean isRunning() {
        return running;
    }

    /**
     * this method used to shutdown network thread if it's running and the keyboard thread.
     * after shutdown the network and keyboard threads the main thread turn off automatically
     *
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
     * this method used to produce message from keyboard
     *
     * @param message String to be produce
     *
     * @throws InterruptedException in case of thread is waiting, sleeping, or otherwise occupied, and the thread is interrupted, either before or during the activity.
     */
    synchronized void produce(String message) throws InterruptedException {
        bridge.put(message);
    }

    /**
     * this method used to consume message using network
     *
     * @return String to be consume
     *
     * @throws InterruptedException in case of thread is waiting, sleeping, or otherwise occupied, and the thread is interrupted, either before or during the activity.
     */
     String consume() throws InterruptedException {
        return bridge.take();
    }

    /**
     * this method used
     *
     * @return boolean if is true the user is connected else is offline
     */
    synchronized boolean isConnected() {
        return network != null;
    }

    public synchronized String toString() {
        return "client " + socket.getLocalAddress() + " on port (" + socket.getLocalPort() + ")";
    }
}
