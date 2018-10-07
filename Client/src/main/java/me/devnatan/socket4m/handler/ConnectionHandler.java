package me.devnatan.socket4m.handler;

import me.devnatan.socket4m.connection.Connection;

public abstract class ConnectionHandler {

    public void handle(String s, Connection c) {
        switch (s) {
            case "connect":
                onConnect(c);
            case "disconnect":
                onDisconnect(c);
            case "fail":
                onFailConnect(c);
            case "reconnect":
                onReconnect(c);
            case "try":
                onTryConnect(c);
        }
    }

    public abstract void onConnect(Connection c);
    public abstract void onDisconnect(Connection c);
    public abstract void onFailConnect(Connection c);
    public abstract void onReconnect(Connection c);
    public abstract void onTryConnect(Connection c);

}
