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
        InputStream fis = Banner.class.getClassLoader().getResourceAsStream("banner.txt");
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
        int commandLength = 15;
        int descriptionLength = 60;
        String helpMessage = "\n";

        helpMessage += "core commands\n";
        helpMessage += insertSeparator('=', "core commands".length());
        helpMessage += "\n";
        helpMessage += "\n";

        helpMessage += "Command";
        helpMessage += giveMeMoreSpace("Command".length(), commandLength);
        helpMessage += "\t\t\t";

        helpMessage += "Description";
        helpMessage += "\n";

        helpMessage += insertSeparator('-', commandLength);
        helpMessage += "\t\t\t";
        helpMessage += insertSeparator('-', descriptionLength);
        helpMessage += "\n";

        Command[] commands = Command.values();
        for (int i = 0; i < commands.length; i++)
            if (commands[i].getSide().equals(side) || commands[i].getSide().equals("both")) {
                helpMessage += commands[i].getcommand();
                helpMessage += giveMeMoreSpace(commands[i].getcommand().length(), commandLength);
                helpMessage += "\t\t\t";

                helpMessage += commands[i].getDescription();
                helpMessage += "\n";
            }

        System.out.println(helpMessage);
    }

    private static String insertSeparator(char character, int number) {
        String out = "";
        for (int i = 0; i < number; i++)
            out += character;
        return out;
    }

    private static String giveMeMoreSpace(int wordLength, int caseLength) {
        String out = "";
        for (int i = 0; i < caseLength - wordLength; i++)
            out += " ";
        return out;
    }
}
