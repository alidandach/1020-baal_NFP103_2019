package context;

import command.Command;

import java.io.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        StringBuilder helpMessage = new StringBuilder();

        helpMessage.append(insertModule("core", '=', commandLength, descriptionLength, side));

        System.out.println(helpMessage);
    }

    private static String insertModule(String functionality, char functionalitySeparator, int commandLength, int descriptionLength, String side) {
        StringBuilder out = new StringBuilder();
        out.append("\n");

        out.append(insertFunctionality("core", '='));

        out.append(insertHeader(commandLength, descriptionLength, '-'));

        out.append(insertDetails(Command.core(), side));

        return out.toString();
    }

    private static String insertFunctionality(String functionality, char separator) {
        StringBuilder out = new StringBuilder();

        out.append(String.format("%-7s", functionality));
        out.append("commands");
        out.append("\n");

        Stream.generate(() -> separator)
                .limit(15)
                .forEach(out::append);

        out.append("\n");
        out.append("\n");

        return out.toString();
    }

    private static String insertHeader(int commandLength, int descriptionLength, char separator) {
        StringBuilder out = new StringBuilder();
        StringBuilder separate = new StringBuilder();

        out.append(String.format("%-23s", "Command"));
        out.append("Description");
        out.append("\n");

        Stream.generate(() -> separator)
                .limit(commandLength)
                .forEach(separate::append);

        out.append(String.format("%-23s", separate.toString()));

        separate.setLength(0);

        Stream.generate(() -> separator)
                .limit(descriptionLength)
                .forEach(separate::append);
        out.append(separate.toString());

        out.append("\n");
        return out.toString();
    }

    private static String insertDetails(Command[] commands, String side) {
        StringBuilder out = new StringBuilder();

        for (int i = 0; i < commands.length; i++)
            if (commands[i].getSide().equals(side) || commands[i].getSide().equals("both")) {
                out.append(String.format("%-23s", commands[i].getCommand()));
                out.append(commands[i].getDescription());
                out.append("\n");
            }

        return out.toString();
    }
}
