package me.devnatan.socket4m.connection;

import lombok.Data;
import me.devnatan.socket4m.handler.ConnectionHandler;
import me.devnatan.socket4m.handler.ErrorHandler;
import me.devnatan.socket4m.handler.MessageHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeUnit;

@Data
public class Connection {

    private final String address;
    private final int port;
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
            Long timeout = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(10);

            channel = SocketChannel.open();
            channel.configureBlocking(false);
            channel.connect(new InetSocketAddress(address, port));

            for (;;) {
                if(System.currentTimeMillis() > timeout) break;
                if (connectionHandler != null){
                    connectionHandler.handle("try", this);
                }
            }

            if (connected && channel.isConnected()) {
                return reconnect();
            }

            if (!channel.isConnected()) {
                if (connectionHandler != null)
                    connectionHandler.handle("fail", this);
                return false;
            }
            else {
                connected = true;
                if (connectionHandler != null)
                    connectionHandler.handle("connect", this);
                return true;
            }


        } catch (Exception e) {
            if(errorHandler != null)
                errorHandler.handle(e.getCause());
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
