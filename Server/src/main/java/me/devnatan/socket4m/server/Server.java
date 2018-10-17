package me.devnatan.socket4m.server;

import me.devnatan.socket4m.server.connection.Connection;
import me.devnatan.socket4m.server.manager.ConnectionManager;
import org.apache.log4j.Logger;

import java.nio.channels.AsynchronousServerSocketChannel;

public interface Server {

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
     * Server ConnectionManager.
     * @return List
     */
    ConnectionManager getConnectionManager();

    /**
     * Starts the server.
     * @throws  IllegalStateException
     *          If the server is already running
     */
    void start() throws IllegalStateException;

    /**
     * Stops the server.
     * @throws  IllegalStateException
     *          If the server is not running yet
     */
    void stop() throws IllegalStateException;

    /**
     * If the server is running and ready to receive connections.
     * @return boolean
     */
    boolean isRunning();

    ServerHeart getHeart();

    default AsynchronousServerSocketChannel channel() {
        return (AsynchronousServerSocketChannel) getConnection().getChannel();
    }

}
