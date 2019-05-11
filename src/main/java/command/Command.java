package command;

import java.util.ArrayList;

public enum Command {
    CLIENTS("both", "core","who", "List all clients connected to server"),
    CONNECT("client","core", "connect", "Connect to the server ex: connect root@192.168.0.10:5555"),
    HELP("both", "core","help", "Print a help message"),
    KILL("server", "core","kill", "kill client ex: kill pc1"),
    QUIT("both", "core","quit", "Exit"),
    START("server", "core","start", "Configure a machine to listen on a specific port ex: start 5555"),
    CHAT_WITH_USER("client", "chat","usr", "chat with another client ex: usr pc1 message");

    private String side;
    private String functionality;
    private String command;
    private String description;

    Command(String side, String functionality, String command, String description) {
        this.side = side;
        this.functionality = functionality;
        this.command = command;
        this.description = description;
    }

    public String getSide() {
        return side;
    }

    public String getCommand() {
        return command;
    }

    public String getDescription() {
        return description;
    }

    public static Command getCommand(String command) {
        Command[] commands = Command.values();
        for (Command value : commands) {
            if (value.getCommand().equals(command))
                return value;
        }
        return null;
    }

    public static Command[] byFunctionality(String type){
        switch (type){
            case "core":return core();
            case "chat":return chat();
            case "group":return group();
            case "file":return file();
            default: return null;
        }
    }

    public static Command[] core(){
        Command[] commands=Command.values();
        ArrayList<Command> out=new ArrayList<>();

        for (Command command : commands)
            if(command.functionality.equals("core"))
                out.add(command);
        return out.toArray(new Command[out.size()]);
    }

    public static Command[] chat(){
        Command[] commands=Command.values();
        ArrayList<Command> out=new ArrayList<>();

        for (Command command : commands)
            if(command.functionality.equals("chat"))
                out.add(command);
        return out.toArray(new Command[out.size()]);
    }

    public static Command[] group(){
        Command[] commands=Command.values();
        ArrayList<Command> out=new ArrayList<>();

        for (Command command : commands)
            if(command.functionality.equals("group"))
                out.add(command);
        return out.toArray(new Command[out.size()]);
    }

    public static Command[] file(){
        Command[] commands=Command.values();
        ArrayList<Command> out=new ArrayList<>();

        for (Command command : commands)
            if(command.functionality.equals("file"))
                out.add(command);
        return out.toArray(new Command[out.size()]);
    }

}
