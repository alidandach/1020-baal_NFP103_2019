package client;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class NetworkInput extends Thread {
    private Client client;

    public NetworkInput(String n, Client c) {
        super(n);
        client = c;
    }

    public Client getClient() {
        return client;
    }

    @Override
    public void run() {
        while (!client.isShutdown()) {
            Socket socket = null;
            InputStream data = null;
            try {
                socket = client.getSocket();
                data = socket.getInputStream();
                String message = new String(data.readAllBytes());
                System.out.println("from server" + message);

            } catch (IOException e) {
                System.out.println("problem with server connection:" + e.getMessage());
                System.exit(-1);
            }
        }
    }
}
