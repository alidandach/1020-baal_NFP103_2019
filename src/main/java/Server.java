import java.util.Vector;

public class Server implements Runnable{
    private Vector<Client> clients;

    public Server(){
        clients=new Vector<Client>();
    }

    public synchronized void addClient(Client c){
        clients.add(c);
    }

    public synchronized void removeClient(Client c){
        clients.remove(c);
    }

    public void run() {

    }
}
