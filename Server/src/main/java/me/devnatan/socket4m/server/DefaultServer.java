package me.devnatan.socket4m.server;

import me.devnatan.socket4m.server.connection.Connection;

import java.io.IOException;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.NetworkChannel;

public abstract class DefaultServer extends AbstractServer {

    protected DefaultServer(Connection connection) {
        super(connection);
    }

    public void start() {
        if(running)
            throw new IllegalStateException("The server is already running");

        try {
            AsynchronousServerSocketChannel assc = AsynchronousServerSocketChannel.open();
            assc.bind(getConnection().getAddress());
            connection.setChannel(assc);
            running = true;
            heart = new ServerHeart(this);
            new Thread(heart, "Socket4M-Server").start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if(!running)
            throw new IllegalStateException("The server is not running yet");

        NetworkChannel ch = getConnection().getChannel();
        if(ch != null && ch.isOpen()) {
            try {
                heart.getServer().channel().close();
                ch.close();
                running = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
