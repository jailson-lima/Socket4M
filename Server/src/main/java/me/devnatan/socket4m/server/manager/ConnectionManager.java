package me.devnatan.socket4m.server.manager;

import lombok.Getter;
import me.devnatan.socket4m.server.connection.ClientConnection;
import me.devnatan.socket4m.server.connection.Connection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ConnectionManager {

    @Getter private final Set<Connection> connections = new HashSet<>();

    public Connection get(SocketChannel socketChannel) {
        return connections.stream().filter(c -> c.getChannel().equals(socketChannel))
                .findFirst().orElse(null);
    }

    public Connection attach(SocketChannel socketChannel) {
        try {
            InetSocketAddress sa = (InetSocketAddress) socketChannel.getRemoteAddress();
            Connection c = new ClientConnection(connections.size() + 1, sa);
            c.setChannel(socketChannel);
            connections.add(c);
            return c;
        } catch (IOException e) {
            e.printStackTrace();
        } return null;
    }

    public Connection detach(SocketChannel socketChannel) {
        Iterator<Connection> iter = connections.iterator();
        while(iter.hasNext()) {
            Connection c = iter.next();
            if(c.getChannel().equals(socketChannel)) {
                iter.remove();
                return c;
            }
        } return null;
    }

}
