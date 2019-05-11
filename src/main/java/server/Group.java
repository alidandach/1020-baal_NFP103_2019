package server;

import java.util.Vector;

/**
 * @author Ali Dandach
 * Date 10/05/2019
 */
public class Group {
    private static int counter = 1;
    private int id;
    private String name;
    private Vector<Client> clients;


    Group(String name) {
        id = counter++;
        this.name = name;
        clients = new Vector<>();
    }

    /**
     * get id of group
     * @return int id of group
     */
    int getId(){
        return id;
    }

    /**
     * get name of group
     * @return String name of group
     */
    String getName(){
        return name;
    }

    /**
     * method used to add newClient to group
     *
     * @param newClient Client to be added
     */
    void addClient(Client newClient) {
        clients.add(newClient);
        for (Client client : clients) {
            if (!client.equals(newClient))
                client.send(newClient.getHostName()+" enter group.");
        }
    }

    /**
     * method to delete specific client
     *
     * @param deletedClient Client to be deleted
     */
    void removeClient(Client deletedClient) {
        clients.remove(deletedClient);
        for (Client client : clients) {
            if (!client.equals(deletedClient))
                client.send(deletedClient.getHostName()+" left group.");
        }
    }

    /**
     * method used to send message foreach client connected to the group
     * @param sender Client who send the message
     * @param message String contain of message
     */
    void broadcast(Client sender, String message) {
        for (Client client : clients) {
            if (!client.equals(sender))
                client.send(sender.getHostName()+" [pc"+sender.getId()+"] say:"+message);
        }
    }
}
