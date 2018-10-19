package me.devnatan.socket4m.server.handler;

import lombok.Value;
import me.devnatan.socket4m.server.Server;
import me.devnatan.socket4m.server.connection.Connection;

import java.io.IOException;
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
                server.getLogger().info("[~] Reading...");
                ByteBuffer bb = (ByteBuffer) attach.get("buffer");
                bb.flip();
                attach.put("action", "write");

                ((AsynchronousSocketChannel) connection.getChannel()).write(bb, attach, this);
                server.getLogger().info("[~>] " + connection.address() + " (size of " + bb.array().length + " bytes)");
                bb.clear();
                break;
            }
            case "write": {
                server.getLogger().info("[~] Writing...");
                ByteBuffer bb = ByteBuffer.allocate(1024);
                server.getLogger().info("[~] Buf content: " + new String(bb.array()));

                attach.put("action", "read");
                attach.put("buffer", bb);

                while(bb.remaining() <= 0) {
                    ((AsynchronousSocketChannel) connection.getChannel()).read(bb, attach, this);
                    server.getLogger().info("[<~] " + connection.address() + " (message " + new String(bb.array()) + ")");
                } break;
            }
            default:
                server.getLogger().info("[~] Unknown action `" + a +"`");
        }
    }

    public void failed(Throwable t, Map<String, Object> attach) {
        try {
            connection.getChannel().close();
            if(server.getConnectionManager().detach((AsynchronousSocketChannel) connection.getChannel()) != null) {
                server.getLogger().info("[<] " + connection.address());
            } else server.getLogger().error("An error occurred during read/write", t);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
