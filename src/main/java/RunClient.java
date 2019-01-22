import java.io.IOException;

public class RunClient {

    public static void main (String... args) throws IOException {
        //display message at startup of program
        Banner.loadBanner();
        Banner.adjustHelpMessage("client");
    }
}
