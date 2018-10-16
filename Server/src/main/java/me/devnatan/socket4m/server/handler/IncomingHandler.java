package me.devnatan.socket4m.server.handler;

import lombok.Value;
import me.devnatan.socket4m.server.Server;
import me.devnatan.socket4m.server.connection.Connection;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.HashMap;
import java.util.Map;

@Value
public class IncomingHandler implements CompletionHandler<AsynchronousSocketChannel, Void> {

    private final Server server;

    public void completed(AsynchronousSocketChannel asc, Void v) {
        Connection c = null;
        AsynchronousServerSocketChannel channel = server.channel();
        if (channel.isOpen()) {
            channel.accept(null, this);
            c = server.getConnectionManager().attach(asc);
            if(c != null) {
                server.getLogger().info("[>] " + c.address());
            }
        }

        if ((asc != null) && (asc.isOpen()) && (c != null)) {
            IOHandler io = new IOHandler(server, c);
            ByteBuffer bb = ByteBuffer.allocate(32);

            Map<String, Object> attach = new HashMap<>();
            attach.put("action", "read");
            attach.put("buffer", bb);

            asc.read(bb, attach, io);
        }
    }

    public void failed(Throwable t, Void v) {
        server.getLogger().error("An error occurred", t);
    }
}
