package me.devnatan.socket4m.server.handler;

import lombok.Value;
import me.devnatan.socket4m.server.Server;
import me.devnatan.socket4m.server.connection.Connection;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Map;

@Value
public class IOHandler implements CompletionHandler<Integer, Map<String, Object>> {

    private final Server server;
    private final Connection connection;

    // READ and WRITE TEST
    public void completed(Integer i, Map<String, Object> attach) {
        String a = (String) attach.get("action");
        switch (a) {
            case "read": {
                ByteBuffer bb = (ByteBuffer) attach.get("buffer");
                bb.flip();
                attach.put("action", "write");

                ((AsynchronousSocketChannel) connection.getChannel()).write(bb, attach, this);
                server.getLogger().info("[~>] " + connection.address() + " (size of " + bb.array().length + " bytes)");
                bb.clear();
                break;
            }
            case "write": {
                ByteBuffer bb = ByteBuffer.allocate(32);

                attach.put("action", "read");
                attach.put("buffer", bb);

                ((AsynchronousSocketChannel) connection.getChannel()).read(bb, attach, this);
                server.getLogger().info("[<~] " + connection.address() + " (size of " + bb.array().length + " bytes)");
                break;
            }
        }
    }

    public void failed(Throwable exc, Map<String, Object> attachment) { }
}
