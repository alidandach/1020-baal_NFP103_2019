import java.io.*;

public class RunServer {

    public static void loadBanner() throws IOException,UnsupportedEncodingException{
        String currentDirectory=new File("").getAbsoluteFile().getParent();
        File file = new File(currentDirectory+"/1020-baal_NFP103_2019/src/main/java/banner.txt");
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        fis.read(data);
        fis.close();
        String banner = new String(data, "UTF-8");
        System.out.println(banner);
    }
    
    public static void adjustHelpMessage(){
        String helpMessage="Command \t Description \n";
        helpMessage+="------- \t ----------- \n";
        Command[] commands = Command.values();
        for(int i=0;i<commands.length;i++)
            if(commands[i].getSide().equals("client") || commands[i].getSide().equals("both"))
                if(commands[i].getcommand().length()>4)
                    helpMessage+=commands[i].getcommand()+" \t "+commands[i].getDescription()+" \n";
                else
                    helpMessage+=commands[i].getcommand()+" \t \t "+commands[i].getDescription()+" \n";

        System.out.println(helpMessage);
    }

    public static void main(String... args) throws IOException {
        //display message at startup of program
        loadBanner();
        adjustHelpMessage();
    }
}
