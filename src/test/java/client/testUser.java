package client;

import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class testUser {

    @Test
    public void setSocket() throws IOException {
        Socket socket = new Socket(InetAddress.getByName("127.0.0.1"), 123);
    }
}
