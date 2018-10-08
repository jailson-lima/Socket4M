package me.devnatan.socket4m.server;

import me.devnatan.socket4m.server.connection.Connection;

public class ExecutableServer extends AbstractServer {

    private ExecutableServer() {
        super(new Connection(4444));
    }

    @Override
    public String getName() {
        return "Main Server";
    }

    public static void main(String[] args) {
        ExecutableServer ms = new ExecutableServer();
        ms.start();
        ms.getLogger().info("Listening on " + ms.getConnection().getPort());
    }

}
