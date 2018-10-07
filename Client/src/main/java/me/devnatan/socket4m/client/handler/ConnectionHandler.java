package me.devnatan.socket4m.client.handler;

import me.devnatan.socket4m.client.connection.Connection;

public abstract class ConnectionHandler {

    public void handle(String s, Connection c) {
        switch (s) {
            case "connect":
                onConnect(c);
                break;
            case "disconnect":
                onDisconnect(c);
                break;
            case "fail":
                onFailConnect(c);
                break;
            case "reconnect":
                onReconnect(c);
                break;
            case "try":
                onTryConnect(c);
                break;
        }
    }

    /**
     * Called when the {@link Connection} establishes a connection.
     * @see Connection#connect()
     * @param c = the connection
     */
    public abstract void onConnect(Connection c);

    /**
     * Called when the {@link Connection} terminates an existing connection.
     * @see Connection#disconnect(boolean)
     * @param c = the connection
     */
    public abstract void onDisconnect(Connection c);

    /**
     * Called when the {@link Connection} attempts to establish a
     * connection to the server but is not successful.
     * @see Connection#connect()
     * @param c = the connection
     */
    public abstract void onFailConnect(Connection c);

    /**
     * Called when the {@link Connection} reconnects.
     * @see Connection#connect()
     * @see Connection#reconnect()
     * @param c = the connection
     */
    public abstract void onReconnect(Connection c);

    /**
     * Called while the {@link Connection} attempts to establish a connection to the server.
     * @see Connection#connect()
     * @param c = the connection
     */
    public abstract void onTryConnect(Connection c);

}
