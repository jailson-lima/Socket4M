package me.devnatan.socket4m.server.executable.commands;

import me.devnatan.socket4m.server.Server;
import me.devnatan.socket4m.server.executable.Core;
import me.devnatan.socket4m.server.executable.command.AbstractCommand;

public class WriteCommand extends AbstractCommand {

    public WriteCommand() {
        super("write");
    }

    public boolean execute(String[] args) {
        Server server = Core.getInstance().getServer();
        if(args.length == 0) {
            server.getLogger().error("Correct usage: /write [message]");
            return false;
        }

        StringBuilder sb = new StringBuilder();
        for(String s : args) sb.append(s).append(" ");
        server.getConnectionManager().write(sb.toString().substring(0, sb.toString().length() - 1));
        return true;
    }
}
