import server.Server;
import user.User;

public class RunApplication {
    public static void main(String[] args) {
        if (args == null || args.length == 0)
            System.out.println("no role defined.\nyou must define a specific role when you launch your application ex:server or client.\n" +
                    "please type 'java -jar file.java server' or 'java -jar file.java client'\n" +
                    "please rerun your application...");
        else
            switch (args[0].toLowerCase()) {
                case "server":
                    new Server();
                    break;
                case "client":
                    new User();
                    break;
                default:
                    System.out.println("no role defined.\nyou must define a specific role when you launch your application ex:server or client.\n" +
                            "please type 'java -jar file.java server' or 'java -jar file.java client'\n" +
                            "please rerun your application...");
            }
    }
}
