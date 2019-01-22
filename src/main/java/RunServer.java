import java.io.*;
import java.util.Scanner;

public class RunServer {
    public static Scanner input;

    private static void getFirstWord(){

    }

    public static void main(String... args) throws IOException {
        //display message at startup of program
        Banner.loadBanner();
        Banner.adjustHelpMessage("server");
        input=new Scanner(System.in);
        do{
            System.out.print("irc > ");
            String command []=input.nextLine().split(" ");
            Command cmd=Command.getCommand(command[0]);
            if(cmd!=null)
            switch (cmd){
                case SHUTDOWN: System.exit(0);break;
                case KILL: break;
                case WHO: break;
                case CONNECT:case QUIT:case HELP:default :Banner.adjustHelpMessage("server");
            }
            else
                Banner.adjustHelpMessage("server");
        }
        while (true);
    }
}
