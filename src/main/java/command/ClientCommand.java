package command;

public enum ClientCommand {
    CONNECT("client", "connect", "Connect to the server ex: connect root@192.168.0.10:5555"),
    QUIT("client", "quit", "Exit"), HELP("both", "help", "Print a help message"),
    WHO("both", "who", "List all clients connected to server"),
    START("both", "start", "Configure a machine to listen on a specific port ");
    private String side;
    private String command;
    private String description;

    ClientCommand(String side, String command, String description) {
        this.side = side;
        this.command = command;
        this.description = description;
    }

    public static ClientCommand getCommand(String command) {
        ClientCommand[] commands = ClientCommand.values();
        for (ClientCommand value : commands) {
            if (value.getcommand().equals(command))
                return value;
        }
        return null;
    }

    public String getSide() { return side; }

    public String getcommand() {
        return command;
    }

    public String getDescription() {
        return description;
    }
}
