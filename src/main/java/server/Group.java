package server;

import java.util.Vector;
import java.util.stream.Stream;

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


    boolean isAdministrator(Client client) {
        return administrator.equals(client);
    }

    /**
     * method used to add newClient to group
     *
     * @param newClient client to be added
     * @return if add successful return true
     */
    boolean addClient(Client newClient) {
        if (isAdministrator(newClient) || members.contains(newClient))
            return false;

        members.add(newClient);
        administrator.send(newClient.getHostName() +" [pc"+newClient.getId()+"]" + " enter " + name + " group.");
        for (Client client : members) {
            if (!client.equals(newClient))
                client.send(newClient.getHostName() +" [pc"+newClient.getId()+"]" + " enter " + name + " group.");
        }
        return true;
    }

    /**
     * method to delete specific user
     *
     * @param deletedClient Client to be deleted
     */
    void removeClient(Client deletedClient) {
        if (deletedClient.equals(administrator)) {
            deletedClient.getServer().removeGroup(deletedClient, this.getName(), false);
            return;
        }

        administrator.send(deletedClient.getHostName()+" [pc"+deletedClient.getId()+"] " + " left group.");
        members.remove(deletedClient);
        for (Client client : members) {
            if (!client.equals(deletedClient))
                client.send(deletedClient.getHostName()+" [pc"+deletedClient.getId()+"] " + " left group.");
        }
    }

    /**
     * method used to check if client member inside group
     *
     * @param client to be checked
     * @return boolean if true client exist inside group
     */
    boolean isMember(Client client) {
        return members.contains(client);
    }

    /**
     * method used to send message foreach user connected to the group
     *
     * @param sender  Client who send the message
     * @param message String contain of message
     */
    void broadcast(Client sender, String message) {
        if (!isAdministrator(sender))
            administrator.send("from group " + name + ":" + sender.getHostName() + " [pc" + sender.getId() + "] say:" + message);
        for (Client client : members) {
            if (!client.equals(sender))
                client.send("from group " + name + ":" + sender.getHostName() + " [pc" + sender.getId() + "] say:" + message);
        }
    }

    /**
     * method used to sharing files on group
     *
     * @param sender Client sender
     * @param data   String
     */
    void sendFile(Client sender, String data) {
        if (!isAdministrator(sender))
            administrator.send(data);
        for (Client client : members)
            if (!client.equals(sender))
                client.send(data);

    }

    /**
     * method used to destroy group
     *
     * @param fromServer if true destroy of group is from server
     */
    void destroy(boolean fromServer) {
        if (fromServer)
            administrator.send(name + " group is deleted by the administrator of server.");
        for (Client member : members)
            if (!fromServer)
                member.send(name + " group is deleted by the owner.");
            else
                member.send(name + " group is deleted by the administrator of server.");
    }

    /**
     * method used to display all members of
     *
     * @return String of data
     */
    String displayMembers() {
        if (members.size() == 0)
            return "sorry no members in this group";

        StringBuilder out = new StringBuilder();
        StringBuilder separate = new StringBuilder();

        out.append("\nmembers\n");
        Stream.generate(() -> "=")
                .limit(10)
                .forEach(separate::append);

        out.append(String.format("%-23s", separate.toString())).append("\n").append("\n");

        separate.setLength(0);


        out.append("administrator is ");
        out.append(administrator.getHostName());
        out.append(" [pc");
        out.append(administrator.getId());
        out.append("]");
        out.append("\n");
        out.append("\n");


        out.append(String.format("%-10s%-35s", "id", "member name")).append("\n");

        Stream.generate(() -> "-")
                .limit(5)
                .forEach(separate::append);
        out.append(String.format("%-10s", separate.toString()));
        separate.setLength(0);

        Stream.generate(() -> "-")
                .limit(15)
                .forEach(separate::append);
        out.append(String.format("%-35s", separate.toString()));
        separate.setLength(0);

        out.append("\n");
        for (Client member : members) {
            out.append(String.format("%-10s%-35s", member.getId(), member.getHostName())).append("\n");
        }

        return out.toString();
    }

    public String toString() {
        return String.format("%-15s%-35s%-35s", id, name, administrator.getHostName());
    }

    String toString(Client c) {
        return String.format("%-15s%-35s%-35s%-15s", id, name, administrator.getHostName(), members.contains(c) || isAdministrator(c));
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
