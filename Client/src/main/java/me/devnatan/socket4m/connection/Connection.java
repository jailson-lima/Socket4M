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
    private int timeout;
    private SocketChannel channel;
    private boolean connected;

    private ConnectionHandler connectionHandler;
    private MessageHandler messageHandler;
    private ErrorHandler errorHandler;

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
            channel = SocketChannel.open();
            channel.configureBlocking(false);
            if (timeout > 0) channel.socket().setSoTimeout(timeout);
            channel.connect(new InetSocketAddress(address, port));

            while (!channel.finishConnect()) {
                if (connectionHandler != null) connectionHandler.handle("try", this);
            }

            if (channel.isConnected()) {
                if (connected) return reconnect();

                connected = true;
                if (connectionHandler != null)
                    connectionHandler.handle("connect", this);
                return true;
            }
        } catch (ConnectException e) {
            if (connectionHandler != null)
                connectionHandler.handle("fail", this);
            if(errorHandler != null) errorHandler.handle(e);
        } catch (Exception e) {
            if(errorHandler != null)
                errorHandler.handle(e);
            connected = false;
        } return false;
    }

    /**
     * Try to reconnect to the server.
     * @return true if the client reconnects successfully.
     */
    public boolean reconnect() {
        if(connected && channel.isConnected()) {
            if(connectionHandler != null)
                connectionHandler.handle("reconnect", this);
            return true;
        } return false;
    }

    /**
     * Terminates the connection to the server.
     * @return true if the connection is terminated.
     */
    public boolean disconnect() {
        if(connected && channel.isConnected()) {
            try {
                channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            connected = false;
            if(connectionHandler != null)
                connectionHandler.handle("disconnect", this);
            return true;
        } return false;
    }
}
