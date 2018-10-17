package me.devnatan.socket4m.server.executable;

import lombok.Data;
import me.devnatan.socket4m.server.HandyServer;
import me.devnatan.socket4m.server.connection.Connection;
import me.devnatan.socket4m.server.executable.command.Command;
import me.devnatan.socket4m.server.executable.commands.StopCommand;
import me.devnatan.socket4m.server.executable.commands.WriteCommand;
import me.devnatan.socket4m.server.manager.CommandManager;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Scanner;

@Data
public class Core implements Runnable {

    private static Core instance;
    private HandyServer server;

    private void loadCommands() {
        CommandManager cm = server.getCommandManager();
        cm.add(new StopCommand());
        cm.add(new WriteCommand());
    }

    public void start(int port, Runnable bef) {
        server = new HandyServer(new Connection(new InetSocketAddress(port)));

        // MODULES
        server.getLogger().info("Loading modules...");
        loadCommands();

        // SERVER
        server.start();
        bef.run();
        new Thread(this, "Socket4M-Core").run();
    }

    public void run() {
        Scanner sc = new Scanner(System.in).useDelimiter("\\n");
        while(server.isRunning()) {
            if(sc.hasNext()) {
                String ne = sc.next().trim();
                if(ne.length() > 0) {
                    String first = ne.split(" ")[0];
                    Command c = server.getCommandManager().get(first);
                    if(c == null) {
                        server.getLogger().info("Command `" + first + "` not found.");
                    } else {
                        String[] sp = ne.split(" ");
                        c.execute(!ne.contains(" ") ? new String[0] : Arrays.copyOfRange(sp, 1, sp.length + 1));
                    }
                }
            }
        }

        sc.close();
        server.getLogger().info("Good bye :*");
    }

    public static Core getInstance() {
        return instance;
    }

    public static void setInstance(Core instance) {
        if(Core.instance != null)
            throw new IllegalStateException("Core instance is already set");
        Core.instance = instance;
    }
}
