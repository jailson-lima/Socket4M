package me.devnatan.socket4m.server.executable;

import java.util.Scanner;

public class Launcher {

    public static void main(String[] args) {
        System.out.println("\n" +
                "   ######   #######   ######  ##    ## ######## ########    ##        ##     ##          ###   \n" +
                        "  ##    ## ##     ## ##    ## ##   ##  ##          ##       ##    ##  ###   ###            ##  \n" +
                        "  ##       ##     ## ##       ##  ##   ##          ##       ##    ##  #### ####    #####    ## \n" +
                        "   ######  ##     ## ##       #####    ######      ##       ##    ##  ## ### ##             ## \n" +
                        "        ## ##     ## ##       ##  ##   ##          ##       ######### ##     ##    #####    ## \n" +
                        "  ##    ## ##     ## ##    ## ##   ##  ##          ##             ##  ##     ##            ##  \n" +
                        "   ######   #######   ######  ##    ## ########    ##             ##  ##     ##          ###   \n"
        );
        System.out.println("Welcome to the Socket4M Executable Server!");
        System.out.println("Enter the port of your server...");
        Scanner s = new Scanner(System.in);
        int port = 4444;
        try {
            port = Integer.parseInt(s.next());
        } catch (NumberFormatException e) {
            System.err.println("Only numbers are acceptable!");
            main(args);
        }

        Core core = new Core();
        core.start(port, () -> {
            Core.setInstance(core);
            core.getServer().getLogger().info("Listening on " + core.getServer().getConnection().getPort() + ".");
        });
    }

}
