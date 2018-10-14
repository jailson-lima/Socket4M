package me.devnatan.socket4m.server.command;

import lombok.Data;

@Data
public abstract class Command implements ICommand {

    private final String name;
    private final String[] aliases;

    public Command(String name) {
        this.name = name;
        this.aliases = new String[0];
    }

    public Command(String name, String... aliases) {
        this.name = name;
        this.aliases = aliases;
    }

}
