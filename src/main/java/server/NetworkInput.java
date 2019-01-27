package server;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class NetworkInput extends Thread {
    private Server server;
    private Socket socket;
    private BufferedInputStream receive;
    private ObjectOutputStream send;


    public NetworkInput(String n, Server s) {
        super(n);
        server = s;
        try {
            socket = server.getServerSocket().accept();
            receive = new BufferedInputStream(socket.getInputStream());
            send = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.exit(-1);
        }
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }


    @Override
    public void run() {
        while (!server.isShutdown()) {
            String message = null;
            try {
                message = new String(receive.readAllBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(message);
            switch (message) {
                case "connect":
                    OnlineClient c = new OnlineClient(socket, "connected !");
                    server.addClient(c);
                    System.out.println("new client is connected.check it by typing who command");
                    System.out.print("irc > ");
                    break;

                case "who":
                    try {
                        send.writeObject(server.listAllClients());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //feedback.flush();
                    break;
                default:
                    try {
                        send.writeObject("sorry only connect and who command working....");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //feedback.flush();
                    break;
            }

        }
    }
}
