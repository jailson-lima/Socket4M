package me.devnatan.socket4m.server.executable.commands;

import me.devnatan.socket4m.server.Server;
import me.devnatan.socket4m.server.command.Command;
import me.devnatan.socket4m.server.executable.Core;

public class StopCommand extends Command {

    public StopCommand() {
        super("stop", "end");
    }

    @Override
    public void execute() {
        Server server = Core.getInstance().getServer();
        if(!server.stop()) {
            server.getLogger().error("Couldn't stop the server!");
        }
    }
}
