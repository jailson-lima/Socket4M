package me.devnatan.socket4m.server.executable.command;

import lombok.Data;

@Data
public abstract class AbstractCommand implements Command {

    protected final String name;
    protected final String[] aliases;

    public AbstractCommand(String name) {
        this.name = name;
        this.aliases = new String[0];
    }

    public AbstractCommand(String name, String... aliases) {
        this.name = name;
        this.aliases = aliases;
    }

}
