public enum Command {
    CONNECT("client","connect", "Connect to the server ex: connect root@192.168.0.10:5555"),
    QUIT("client","quit", "Exit"),
    SHUTDOWN("server","shutdown","shutdown the server"),
    KILL("server","kill","kill client ex: kill pc1"),
    HELP("both","help", "Print a help message"),
    WHO("both","who", "list all clients connected to server"),;

    private String side;
    private String command;
    private String description;

    Command(String side,String command, String description) {
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

    public static Command getCommand(String command) {
        Command[] commands = Command.values();
        for (Command value : commands) {
            if(value.getcommand().equals(command))
                return value;
        }
        return null;
    }

}
