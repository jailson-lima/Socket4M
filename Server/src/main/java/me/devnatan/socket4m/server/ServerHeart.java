package me.devnatan.socket4m.server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.devnatan.socket4m.server.handler.IncomingHandler;

import java.nio.channels.CompletionHandler;

@AllArgsConstructor
public class ServerHeart implements Runnable {

    @Getter private final Server server;
    @Getter private final CompletionHandler handler;

    public ServerHeart(Server server) {
        this.server = server;
        handler = new IncomingHandler(server);
    }

    public void run() {
        try {
            server.channel().accept(null, handler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
