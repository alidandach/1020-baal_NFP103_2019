package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class OnlineClient extends Thread {
    private Socket broadcast;
    private OutputStream output;
    private InputStream input;
    private String message;

    public OnlineClient(Socket socket,String m) {
        broadcast = socket;
        message=m;
        try {
            input = broadcast.getInputStream();
            output = broadcast.getOutputStream();
        } catch (IOException e) {
            System.out.println(e.getCause());
        }
    }

    public void run () {
        try {
            output.write(message.getBytes());
            output.flush();
            output.close();

            if (broadcast!=null) {
                broadcast.close();
            }
        }
        catch(IOException e){
            System.out.println("OOps! Unable to disconnect!");
        }
    }

    public String toString(){
        return broadcast.getInetAddress().toString();
    }
}
