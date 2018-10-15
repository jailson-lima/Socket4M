package me.devnatan.socket4m.server;

import me.devnatan.socket4m.server.connection.Connection;

import java.io.IOException;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.ServerSocketChannel;

public abstract class DefaultServer extends AbstractServer {

    public DefaultServer(Connection connection) {
        super(connection);
    }

    @Override
    public boolean start() {
        try {
            AsynchronousServerSocketChannel assc = AsynchronousServerSocketChannel.open();

            assc.bind(getConnection().getAddress());
            connection.setChannel(assc);
            running = true;
            heart = new ServerHeart(this);
            new Thread(heart, "Socket4M-Heart").start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean stop() {
        ServerSocketChannel ssc = (ServerSocketChannel) getConnection().getChannel();
        if(ssc != null && ssc.isOpen()) {
            try {
                heart.getServer().channel().close();
                ssc.close();
                running = false;
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

}
