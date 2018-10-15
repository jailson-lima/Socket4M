package me.devnatan.socket4m.server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.devnatan.socket4m.server.handler.IncomingHandler;

@AllArgsConstructor
public class ServerHeart implements Runnable {

    @Getter private final Server server;

    @Override
    public void run() {
        try {
            server.channel().accept(null, new IncomingHandler(server));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
