import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * this class is non functionality it's just for organisation
 */
public class Banner {
    /**
     * this method to read file(banner.txt) and print the output on the terminal.It's used at startup
     *
     * @throws IOException
     * @throws UnsupportedEncodingException
     */
    public static void loadBanner() throws IOException, UnsupportedEncodingException {
        String currentDirectory = new File("").getAbsoluteFile().getParent();
        File file = new File(currentDirectory + "/1020-baal_NFP103_2019/src/main/java/banner.txt");
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        fis.read(data);
        fis.close();
        String banner = new String(data, "UTF-8");
        System.out.println(banner);
    }


    /**
     * this method used to print the help message.it's take side as parameter and print help message correspond (server or client)
     *
     * @param side (server,client)
     */
    public static void adjustHelpMessage(String side) {
        String helpMessage = "\n";
        helpMessage += "Command \t Description \n";
        helpMessage += "------- \t ----------- \n";
        Command[] commands = Command.values();
        for (int i = 0; i < commands.length; i++)
            if (commands[i].getSide().equals(side) || commands[i].getSide().equals("both"))
                if (commands[i].getcommand().length() > 4)
                    helpMessage += commands[i].getcommand() + " \t " + commands[i].getDescription() + " \n";
                else
                    helpMessage += commands[i].getcommand() + " \t \t " + commands[i].getDescription() + " \n";

        System.out.println(helpMessage);
    }
}
