package server;

import command.Command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class OnlineClient extends Thread {
    private Server server;
    private Socket socket;
    private PrintWriter output;
    private BufferedReader input;

    public OnlineClient(Server s, Socket socket) {
        server = s;
        this.socket = socket;
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println(e.getCause());
        }
    }

    public String getHostName(){
        return socket.getLocalAddress().getHostName();
    }

    public void disconnect(){
        try {
            input.close();
            output.close();
            socket.close();
        } catch (IOException e) {
            System.out.println("problem when closing client connection:" + e.getMessage());
        }
    }

    public void run() {
        String request;
        try {

            while (!this.isInterrupted()) {
                // receive the command from client
                request = input.readLine();

                //parse command
                Command cmd = Command.getCommand(request);

                //handle command
                switch (cmd) {
                    case WHO:
                        output.println(server.listAllClients());
                        break;
                }

            }
        } catch (IOException e) {
            System.out.println("problem with client connection:" + e.getMessage());
            server.removeClient(getHostName());
        } finally {
            disconnect();
        }

    }

    public String toString() {
        return socket.getLocalAddress().getHostName() + "\t\t\t" + socket.getLocalAddress().getHostAddress() + "\t\t\t" + socket.getPort();
    }
}
