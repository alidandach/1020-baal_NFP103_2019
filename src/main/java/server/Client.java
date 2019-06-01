package server;

import command.Command;
import flags.Identity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import security.Symmetric;
import validation.Validation;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Matcher;

public class Client implements Comparable<Client> {
    private static int counter = 1;
    private int id;
    private byte[] secretKey;
    private Server server;
    private Socket socket;
    private volatile boolean connected;
    private volatile boolean disconnectFromKeyboard;
    private BlockingQueue<String> bridge;

    private final static Logger logger = LogManager.getLogger(Client.class);

    Client(Server s, Socket socket, byte[] key) {
        id = counter++;
        secretKey = key;
        server = s;
        this.socket = socket;
        connected = true;
        bridge = new ArrayBlockingQueue<>(1);

        //initialize transmitter
        //wait to consume message
        //send to user
        //check if need to turn off this thread
        Thread transmitter = new Thread(() -> {
            PrintWriter output;
            try {
                output = new PrintWriter(socket.getOutputStream(), true);
                String request;
                while (connected) {
                    //wait to consume message
                    request = bridge.take();

                    //send to user
                    String d = Base64.getEncoder().encodeToString(Symmetric.encrypt(request.getBytes(), Base64.getEncoder().encodeToString(this.secretKey), new byte[16]));
                    output.println(d);

                    //check if need to turn off this thread
                    if (request.equals(Command.QUIT.getCommand()))
                        break;
                }
            } catch (IOException e) {
                Interrupt();
                newLine();
                logger.error("io exception\t----->\t" + e.getMessage());
            } catch (InterruptedException e) {
                Interrupt();
                newLine();
                logger.error("Thread Exception\t----->\t" + e.getMessage());
            } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ignored) {
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        transmitter.start();

        //initialize receiver
        // receive the command from user
        //parse command
        //handle command
        Thread receiver = new Thread(() -> {
            BufferedReader input = null;
            try {
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String response;
                while (connected) {
                    // receive the command from user
                    response = input.readLine();

                    //security check
                    if (response == null)
                        return;

                    String dataDecrypted = new String(Symmetric.decrypt(Base64.getDecoder().decode(response), Base64.getEncoder().encodeToString(this.secretKey), new byte[16]));


                    //request contains command and parameters
                    String[] command = dataDecrypted.trim().split(" ");

                    //parse command
                    Command cmd = Command.getCommand(command[0]);

                    //handle command
                    if (cmd != null) {
                        switch (cmd) {
                            case CLIENTS:
                                bridge.put(server.displayClients());
                                break;
                            case QUIT:
                                server.removeClient(this, false);

                                if (!disconnectFromKeyboard) {
                                    System.out.println("\nThere is a user left");
                                    startPrefix();
                                }

                                break;
                            case CHAT_WITH_USER:
                                if (command.length >= 3) {
                                    //parse id of pc
                                    Matcher matcher = Validation.CLIENT.getPattern().matcher(command[1]);
                                    if (matcher.matches()) {
                                        String[] pcs = command[1].split("pc");
                                        int partnerId = Integer.parseInt(pcs[1]);
                                        if (partnerId != id) {
                                            Client c = server.getClientById(partnerId);
                                            StringBuilder message = new StringBuilder();
                                            for (int i = 2; i < command.length; i++)
                                                message.append(command[i]).append(" ");
                                            if (c != null)
                                                c.send(getHostName() + " [pc" + id + "] say:" + message.toString());
                                        }

                                    }
                                }
                                break;

                            case LIST_GROUPS:
                                bridge.put(server.displayGroups(this));
                                break;

                            case CREATE_GROUP:
                                if (command.length == 2) {
                                    if (server.containsGroup(command[1]))
                                        bridge.put("server say: existing group tried with another group name");
                                    else if (server.addGroup(new Group(command[1], this))) {
                                        System.out.println("\nnew group added");
                                        bridge.put("server say:new group added");
                                    } else {
                                        System.out.println("\nproblem occur when create new group");
                                        bridge.put("server say:problem occur when create new group");
                                    }
                                    startPrefix();
                                }
                                break;

                            case JOIN_GROUP:
                                if (command.length == 2) {
                                    if (server.joinGroup(this, command[1]))
                                        bridge.put("server say:join successful to " + command[1]);
                                    else
                                        bridge.put("server say:join unsuccessful to " + command[1]);
                                }
                                break;

                            case EXIT_GROUP:
                                if (command.length == 2)
                                    if (server.exitGroup(this, command[1]))
                                        bridge.put("server say:exit successful to " + command[1]);
                                    else
                                        bridge.put("server say:exit unsuccessful to " + command[1]);
                                break;

                            case DELETE_GROUP:
                                if (command.length == 2)
                                    if (server.removeGroup(this, command[1], false)) {
                                        bridge.put("server say:delete group " + command[1]);
                                        System.out.println("\ngroup" + command[1] + " deleted");
                                        startPrefix();
                                    } else
                                        bridge.put("server say:we could not delete group " + command[1]);
                                break;
                            case CHAT_ON_GROUP:
                                if (command.length >= 3) {
                                    //parse id of group
                                    Matcher matcher = Validation.GROUP.getPattern().matcher(command[1]);
                                    if (matcher.matches()) {
                                        String[] groupTarget = command[1].split("grp");
                                        int groupId = Integer.parseInt(groupTarget[1]);
                                        Group group = server.getGroupById(groupId);

                                        StringBuilder message = new StringBuilder();
                                        for (int i = 2; i < command.length; i++)
                                            message.append(command[i]).append(" ");
                                        if (group != null)
                                            group.broadcast(this, message.toString());
                                        else
                                            this.send("server say: group not exist!");
                                    }
                                }
                                break;
                            case SEND_FILE:
                                if (command.length > 2) {
                                    //parse id of pc
                                    Matcher matcherUser = Validation.CLIENT.getPattern().matcher(command[1]);
                                    //parse id of group
                                    Matcher matcherGroup = Validation.GROUP.getPattern().matcher(command[1]);

                                    //parse file data
                                    String[] file = dataDecrypted.trim().split(flags.File.DATA_SEPARATOR.getValue());

                                    if (matcherUser.matches()) {
                                        String[] pcs = command[1].split("pc");
                                        int partnerId = Integer.parseInt(pcs[1]);
                                        if (partnerId != id) {
                                            Client c = server.getClientById(partnerId);
                                            if (c != null) {
                                                c.send(Command.SEND_FILE.getCommand() + " " + flags.File.DATA_SEPARATOR.getValue() + this.getHostName() + flags.File.DATA_SEPARATOR.getValue() + this.id + flags.File.DATA_SEPARATOR.getValue() + file[1] + flags.File.DATA_SEPARATOR.getValue() + file[2]);
                                                bridge.put("server say:file sent successfully");
                                            } else
                                                bridge.put("server say:client not found");

                                        }
                                    } else if (matcherGroup.matches()) {
                                        String[] groups = command[1].split("grp");
                                        int groupId = Integer.parseInt(groups[1]);
                                        if (server.groupIsExist(groupId)) {
                                            Group group = server.getGroupById(groupId);
                                            group.sendFile(this, Command.SEND_FILE.getCommand() + " " + flags.File.DATA_SEPARATOR.getValue() + group.getName() + "_group" + "_by_" + this.getHostName() + flags.File.DATA_SEPARATOR.getValue() + this.id + flags.File.DATA_SEPARATOR.getValue() + file[1] + flags.File.DATA_SEPARATOR.getValue() + file[2]);
                                            bridge.put("server say:file sent successfully");
                                        } else
                                            this.send("server say:sorry this group not exist");

                                    }
                                }
                                break;

                            case MEMBERS_OF_GROUP:
                                if (command.length == 2) {
                                    Group group = server.getGroup(command[1]);
                                    if (group != null)
                                        bridge.put(group.displayMembers());
                                    else
                                        bridge.put("server say:sorry " + command[1] + "not found!");
                                }
                                break;
                        }
                    }

                }
            } catch (IOException | InterruptedException e) {
                Interrupt();
                System.out.println("\nThere is a user left");
                startPrefix();
            } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ignored) {
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (input != null)
                        input.close();
                } catch (IOException e) {
                    startPrefix();
                }
            }

        });
        receiver.start();
    }

    /**
     * method return id of current user
     *
     * @return int id of user
     */
    int getId() {
        return id;
    }

    /**
     * getter for server
     *
     * @return Server
     */
    Server getServer() {
        return server;
    }

    /**
     * method used to adjusting console style
     */
    private void startPrefix() {
        String prefix = "irc >";
        System.out.print(prefix);
    }

    /**
     * method used to put new line on console server
     */
    private void newLine() {
        System.out.println();
    }

    /**
     * method to return host name of current user
     *
     * @return String indicate the host name of user
     */
    String getHostName() {
        return socket.getLocalAddress().getHostName();
    }

    /**
     * this method used to disconnect the current user
     */
    void disconnect() {
        try {
            connected = false;
            bridge.put(Command.QUIT.getCommand());

        } catch (InterruptedException e) {
            logger.error("Interrupted exception in thread user\t----->\t" + e.getMessage());
        }
    }

    /**
     * this method used by the admin of server to kill specific user
     */
    void disconnectFromKeyboard() {
        disconnectFromKeyboard = true;
    }


    /**
     * every user connected have thread.this method used to kill current user(2 thread in and out) and remove it from clients list on the server
     */
    private void Interrupt() {
        bridge.clear();
        server.destroyClient(this);
        server.removeClient(this, false);
    }

    /**
     * this method used to send message (answer) to the user
     *
     * @param answer String to be send towards user
     */
    void send(String answer) {
        bridge.add(answer);
    }


    public String toString() {
        return String.format("%-15s%-35s%-35s%-15s", id, getHostName(), socket.getLocalAddress().getHostAddress(), socket.getPort());
    }

    @Override
    public int compareTo(Client o) {
        return Integer.compare(id, o.getId());
    }

    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (!Client.class.isAssignableFrom(obj.getClass()))
            return false;

        return id == ((Client) obj).getId();
    }
}
