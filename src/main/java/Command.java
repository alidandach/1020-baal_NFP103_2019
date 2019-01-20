public enum Command {
    CONNECT("client","connect", "Connect to a client"),
    QUIT("client","quit", "Exit"),
    SHUTDOWN("server","shutdown","shutdown the server"),
    KILL("server","kill","kill client"),
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
}
