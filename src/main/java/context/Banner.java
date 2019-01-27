package context;

import command.Command;

import java.io.*;

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
    public static void loadBanner() {
        InputStream fis =Banner.class.getClassLoader().getResourceAsStream("banner.txt");;
        byte[] data = new byte[0];
        try {
            data = new byte[fis.available()];
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fis.read(data);
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String banner = null;
        try {
            banner = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
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
        helpMessage += "------- \t ------------------------------------ \n";
        Command[] commands = Command.values();
        for (int i = 0; i < commands.length; i++)
            if (commands[i].getSide().equals(side) || commands[i].getSide().equals("both"))
                if (commands[i].getcommand().length() > 6)
                    helpMessage += commands[i].getcommand() + " \t " + commands[i].getDescription() + " \n";
                else
                    helpMessage += commands[i].getcommand() + " \t \t " + commands[i].getDescription() + " \n";

        System.out.println(helpMessage);
    }
}
