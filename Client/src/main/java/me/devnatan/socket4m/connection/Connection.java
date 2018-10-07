package me.devnatan.socket4m.connection;

import lombok.Data;
import me.devnatan.socket4m.handler.ConnectionHandler;
import me.devnatan.socket4m.handler.ErrorHandler;
import me.devnatan.socket4m.handler.MessageHandler;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

@Data
public class Connection {

    private final String address;
    private final int port;

    // SOCKET
    private int timeout;
    private SocketChannel channel;

    // HANDLERS
    private ConnectionHandler connectionHandler;
    private MessageHandler messageHandler;
    private ErrorHandler errorHandler;

    // RECONNECT
    private boolean reconnectTrying;
    private int reconnectAttempts;
    private int reconnectTries = 10;

    /**
     * Open and connect a socket channel.
     * This channel is pre-configured to be non-blocking
     *
     * {@link ConnectionHandler} are called here if they are defined.
     * The {@link ConnectionHandler#onTryConnect(Connection)} can be called multiple times.
     *
     * If the client is already connected instead of calling the
     * first connection method, the reconnection method is called.
     *
     * @return true if the connection has been established completely.
     */
    public boolean connect() {
        try {
            if(channel == null || !channel.isOpen()) {
                channel = SocketChannel.open();
                channel.configureBlocking(false);
                if (timeout > 0) channel.socket().setSoTimeout(timeout);
            }

            if(channel.isOpen()) {
                channel.connect(new InetSocketAddress(address, port));

                while (!channel.finishConnect()) {
                    if (connectionHandler != null) connectionHandler.handle("try", this);
                }

                if(channel.isConnected()) {
                    if (connectionHandler != null) connectionHandler.handle(reconnectTrying ? "reconnect" : "connect", this);
                    reconnectTrying = false;
                    return true;
                }
            }
        } catch (ConnectException e) {
            if (connectionHandler != null)
                connectionHandler.handle("fail", this);
            if (errorHandler != null) errorHandler.handle(e);
        } catch (IOException e) {
            reconnect();
        } catch (Exception e) {
            if(errorHandler != null) errorHandler.handle(e);
        } return false;
    }

    /**
     * Try to reconnect to the server.
     * @return true if the client reconnects successfully.
     */
    public boolean reconnect() {
        do {
            reconnectTrying = true;
            if(connect()) {
                reconnectTrying = false;
                reconnectAttempts = 0;
                return true;
            }

            reconnectAttempts++;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (reconnectAttempts != reconnectTries);
        return false;
    }

    /**
     * Terminates the connection to the server.
     * @return true if the connection is terminated.
     */
    public boolean disconnect(boolean silent) {
        if(!channel.isOpen())
            throw new IllegalStateException("Channel must be open to disconnect");

        if(channel.isConnected()) {
            try {
                channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(!silent && connectionHandler != null) connectionHandler.handle("disconnect", this);
            return true;
        } return false;
    }
}
