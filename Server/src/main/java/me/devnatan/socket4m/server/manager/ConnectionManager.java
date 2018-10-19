package me.devnatan.socket4m.server.manager;

import lombok.Getter;
import me.devnatan.socket4m.server.connection.Connection;
import me.devnatan.socket4m.server.executable.Core;
import me.devnatan.socket4m.server.handler.IOHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class ConnectionManager {

    @Getter private final Set<Connection> connections = Collections.synchronizedSet(new HashSet<>());

    public Connection get(AsynchronousSocketChannel channel) {
        return connections.stream().filter(c -> c.getChannel().equals(channel))
                .findFirst().orElse(null);
    }

    public Connection attach(AsynchronousSocketChannel channel) {
        try {
            InetSocketAddress sa = (InetSocketAddress) channel.getRemoteAddress();
            Connection c = new Connection(sa);
            c.setChannel(channel);
            c.setHandler(new IOHandler(Core.getInstance().getServer(), c));
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

    public void write(String s) {
        ByteBuffer bb = ByteBuffer.wrap(s.getBytes());
        Map<String, Object> attach = new TreeMap<>();
        attach.put("buffer", bb);
        attach.put("action", "write");

        CompletableFuture.supplyAsync(() -> {
            int i = 0;
            for(Connection c : connections) {
                ((AsynchronousSocketChannel) c.getChannel()).write(bb, attach, c.getHandler());
                i++;
            }

            return i;
        }).whenComplete((i, e) -> Core.getInstance().getServer().getLogger().info("Written sucessfully to " + i + " client(s)."));
    }

}
