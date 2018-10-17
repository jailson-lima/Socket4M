package me.devnatan.socket4m.server.executable.command;

public interface Command {

    String getName();

    String[] getAliases();

    boolean execute(String[] args);

}
