package me.devnatan.socket4m.server.manager;

import lombok.Getter;
import me.devnatan.socket4m.server.connection.Connection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ConnectionManager {

    @Getter private final Set<Connection> connections = new HashSet<>();

    public Connection get(AsynchronousSocketChannel channel) {
        return connections.stream().filter(c -> c.getChannel().equals(channel))
                .findFirst().orElse(null);
    }

    public Connection attach(AsynchronousSocketChannel channel) {
        try {
            InetSocketAddress sa = (InetSocketAddress) channel.getRemoteAddress();
            Connection c = new Connection(sa);
            c.setChannel(channel);
            if(connections.add(c)) return c;
        } catch (IOException e) {
            e.printStackTrace();
        } return null;
    }

    public Connection detach(AsynchronousSocketChannel channel) {
        Iterator<Connection> iter = connections.iterator();
        while(iter.hasNext()) {
            Connection c = iter.next();
            if(c.getChannel().equals(channel)) {
                iter.remove();
                return c;
            }
        } return null;
    }

}
