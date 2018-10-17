package me.devnatan.socket4m.server.executable.commands;

import me.devnatan.socket4m.server.executable.Core;
import me.devnatan.socket4m.server.executable.command.AbstractCommand;

public class StopCommand extends AbstractCommand {

    public StopCommand() {
        super("stop", "end");
    }

    public boolean execute(String[] args) {
        Core.getInstance().getServer().stop();
        return true;
    }
}
