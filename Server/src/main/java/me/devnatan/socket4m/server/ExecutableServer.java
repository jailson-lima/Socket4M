package me.devnatan.socket4m.server;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.devnatan.socket4m.server.connection.Connection;
import me.devnatan.socket4m.server.manager.CommandManager;

@Data
@EqualsAndHashCode(callSuper = true)
public class ExecutableServer extends DefaultServer {

    protected final CommandManager commandManager = new CommandManager();

    public ExecutableServer(Connection connection) {
        super(connection);
    }

}
