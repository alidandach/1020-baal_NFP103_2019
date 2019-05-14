import server.Server;
import user.User;

public class RunApplication {
    public static void main(String[] args){
        if(args==null || args.length==0)
            System.out.println("no role defined. please type 'file.java server' or 'file.java client' ");
        else
            switch (args[0]){
                case "server":new Server();break;
                case "client":new User();break;
                default: System.out.println("no role defined. please type 'file.java server' or 'file.java client' ");
            }
    }
}
