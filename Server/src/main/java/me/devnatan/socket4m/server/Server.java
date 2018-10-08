package me.devnatan.socket4m.server;

import me.devnatan.socket4m.server.connection.Connection;
import me.devnatan.socket4m.server.manager.ConnectionManager;
import org.apache.log4j.Logger;

public interface Server {

    /**
     * A name that you want to assign to the server.
     * @return String
     */
    String getName();

    /**
     * The Server Connection.
     * @return Connection
     */
    Connection getConnection();

    /**
     * The server logger.
     * @return Logger
     */
    Logger getLogger();

    /**
     * Server's ConnectionManager.
     * @return List
     */
    ConnectionManager getConnectionManager();

    /**
     * Starts the server.
     * @return true if the server has started correctly.
     */
    boolean start();

    /**
     * Stops the server.
     * @return true if the server has been disconnected correctly.
     */
    boolean stop();

}
