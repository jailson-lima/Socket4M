package me.devnatan.socket4m.connection;

import lombok.Data;
import me.devnatan.socket4m.handler.ConnectionHandler;
import me.devnatan.socket4m.handler.ErrorHandler;
import me.devnatan.socket4m.handler.MessageHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

@Data
public class Connection {

    private final String address;
    private final int port;
    private SocketChannel channel;
    private boolean connected;

    private ConnectionHandler connectionHandler;
    private MessageHandler messageHandler;
    private ErrorHandler errorHandler;

    public boolean connect() {
        try {
            channel = SocketChannel.open();
            channel.configureBlocking(false);
            channel.connect(new InetSocketAddress(address, port));

            while (!channel.finishConnect()) {
                if (connectionHandler != null) connectionHandler.handle("try", this);
            }

            if (!channel.isConnected()) {
                if (connectionHandler != null)
                    connectionHandler.handle("fail", this);
                return false;
            }

            if (connected && channel.isConnected())
                return reconnect();
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

    public boolean reconnect() {
        if(connected && channel.isConnected()) {
            if(connectionHandler != null)
                connectionHandler.handle("reconnect", this);
            return true;
        } return false;
    }

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
