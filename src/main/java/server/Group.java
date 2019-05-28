package server;

import java.util.Vector;

/**
 * @author Ali Dandach
 * Date 10/05/2019
 */
public class Group implements Comparable<Group> {
    private static int counter = 1;
    private int id;
    private String name;
    private Client administrator;
    private Vector<Client> members;


    Group(String name, Client admin) {
        id = counter++;
        this.name = name;
        administrator = admin;
        members = new Vector<>();
    }

    /**
     * get id of group
     *
     * @return int id of group
     */
    int getId() {
        return id;
    }

    /**
     * get name of group
     *
     * @return String name of group
     */
    String getName() {
        return name;
    }

    /**
     * get administrator of group
     *
     * @return Client represent administrator of group
     */
    Client getAdministrator() {
        return administrator;
    }

    boolean isAdministrator(Client client) {
        return administrator.equals(client);
    }

    /**
     * method used to add newClient to group
     *
     * @param newClient Client to be added
     */
    void addClient(Client newClient) {
        members.add(newClient);
        for (Client client : members) {
            if (!client.equals(newClient))
                client.send(newClient.getHostName() + " enter group.");
        }
    }

    /**
     * method to delete specific user
     *
     * @param deletedClient Client to be deleted
     */
    void removeClient(Client deletedClient) {
        members.remove(deletedClient);
        for (Client client : members) {
            if (!client.equals(deletedClient))
                client.send(deletedClient.getHostName() + " left group.");
        }
    }

    /**
     * method used to send message foreach user connected to the group
     *
     * @param sender  Client who send the message
     * @param message String contain of message
     */
    void broadcast(Client sender, String message) {
        for (Client client : members) {
            if (!client.equals(sender))
                client.send(sender.getHostName() + " [pc" + sender.getId() + "] say:" + message);
        }
    }

    /**
     * method used to destroy group
     *
     */
    void destroy(){
        for (Client member : members)
            member.send(" group is deleted by the owner.");
    }

    public String toString() {
        return String.format("%-15s%-35s%-35s", id, name, administrator.getHostName());
    }

    String toString(Client c) {
        return String.format("%-15s%-35s%-35s%-15s", id, name, administrator.getHostName(), members.contains(c));
    }

    @Override
    public int compareTo(Group o) {
        return name.compareTo(o.getName());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (!Group.class.isAssignableFrom(obj.getClass()))
            return false;

        return ((Group) obj).compareTo(this) == 0;
    }
}
