package me.devnatan.socket4m.server.executable;

import lombok.Data;
import me.devnatan.socket4m.server.AbstractServer;
import me.devnatan.socket4m.server.Server;
import me.devnatan.socket4m.server.command.Command;
import me.devnatan.socket4m.server.connection.Connection;
import me.devnatan.socket4m.server.executable.commands.StopCommand;
import me.devnatan.socket4m.server.manager.CommandManager;

import java.util.Scanner;

@Data
public class Core implements Runnable {

    private static Core instance;
    private Server server;

    private void loadCommands() {
        CommandManager cm = server.getCommandManager();
        cm.add(new StopCommand());
    }

    public void start(int port, Runnable bef) {
        server = new AbstractServer(new Connection(port)) {
            public String getName() {
                return "ExecutableServer";
            }
        };

        // MODULES
        server.getLogger().info("Loading modules...");
        loadCommands();

        // SERVER
        server.start();
        bef.run();
        new Thread(this, "Socket4M-Core").run();
    }

    public void run() {
        server.getLogger().info("Waiting for connections...");
        Scanner s = new Scanner(System.in);
        while(server.isRunning()) {
            if(s.hasNext()) {
                String ne = s.next().trim();
                if(ne.length() > 0) {
                    Command c = server.getCommandManager().get(ne);
                    if(c == null) {
                        server.getLogger().info("Command `" + ne + "` not found.");
                    } else {
                        c.execute();
                    }
                }
            }
        }

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
