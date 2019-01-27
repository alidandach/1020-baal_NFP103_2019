package command;

public enum ServerCommand {
    SHUTDOWN("server","shutdown","shutdown the server"),
    KILL("server","kill","kill client ex: kill pc1"),
    HELP("both","help", "Print a help message"),
    WHO("both","who", "List all clients connected to server"),
    START("both","start","Configure a machine to listen on a specific port ");

    private String side;
    private String command;
    private String description;

    ServerCommand(String side,String command, String description) {
        this.side=side;
        this.command = command;
        this.description = description;
    }

    public String getSide(){
        return side;
    }

    public String getcommand() {
        return command;
    }

    public String getDescription() {
        return description;
    }

    public static ServerCommand getCommand(String command) {
        ServerCommand[] commands = ServerCommand.values();
        for (ServerCommand value : commands) {
            if(value.getcommand().equals(command))
                return value;
        }
        return null;
    }
}
