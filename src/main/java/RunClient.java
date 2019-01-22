import java.io.IOException;
import java.util.Scanner;

public class RunClient {

    private static Scanner input;

    public static void main (String... args) throws IOException {
        //display message at startup of program
        Banner.loadBanner();
        Banner.adjustHelpMessage("client");
        input=new Scanner(System.in);
        do{
            System.out.print("irc > ");
            String command []=input.nextLine().split(" ");
            Command cmd=Command.getCommand(command[0]);
            if(cmd!=null)
                switch (cmd){
                    case CONNECT:break;
                    case QUIT: System.exit(0);break;
                    case WHO: break;
                    case SHUTDOWN: case KILL:case HELP:default:Banner.adjustHelpMessage("client");
                }
            else
                Banner.adjustHelpMessage("client");
        }
        while (true);
    }
}
