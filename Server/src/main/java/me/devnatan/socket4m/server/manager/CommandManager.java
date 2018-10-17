package me.devnatan.socket4m.server.manager;

import lombok.Getter;
import me.devnatan.socket4m.server.executable.command.Command;

import java.util.LinkedList;
import java.util.List;

public class CommandManager {

    @Getter private final List<Command> commandList = new LinkedList<>();

    public Command get(String s) {
        return commandList.stream()
                .filter(c -> c.getName().equalsIgnoreCase(s))
                .findFirst().orElse(null);
    }

    public void add(Command c) {
        commandList.add(c);
    }

}
