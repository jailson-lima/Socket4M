package me.devnatan.socket4m.server;

import me.devnatan.socket4m.server.connection.Connection;

public class WebsocketServer extends AbstractServer {

    public WebsocketServer(Connection connection) {
        super(connection);
    }

    @Override
    public String getName() {
        return "Websocket Server";
    }

    @Override
    public boolean start() {
        return false;
    }

    @Override
    public boolean stop() {
        return false;
    }
}
