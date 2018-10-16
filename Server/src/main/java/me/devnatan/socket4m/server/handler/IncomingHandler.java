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
        // the server accepts a connection
        Connection c = null;
        AsynchronousServerSocketChannel channel = server.channel();

        // verifies that the connection is open to perform the attachment procedures for this connection
        if (channel.isOpen()) {
            // defines the handle of the connection as this class.
            channel.accept(null, this);

            // attaches the connection in the connection manager
            c = server.getConnectionManager().attach(asc);
            if(c != null) {
                /*
                    if the connection was properly attached it will return the same object, but already attached.
                    if not, it will return null, something went wrong at the time of attaching the connection.
                 */
                server.getLogger().info("[>] " + c.address());
            }
        }

        /*
            check if the client sent something at the time of connection,
            it may be a successful connection packet for example.
            verify that the connection is open and
            that the connection object is not null (if it was attached correctly)
            because this method is not synchronized.
         */
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
