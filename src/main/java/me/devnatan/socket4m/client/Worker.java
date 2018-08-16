package me.devnatan.socket4m.client;

import it.shadow.events4j.EventEmitter;
import it.shadow.events4j.argument.Argument;
import it.shadow.events4j.argument.Arguments;
import lombok.Getter;
import lombok.Setter;
import me.devnatan.socket4m.enums.SocketCloseReason;
import me.devnatan.socket4m.handler.def.DefaultReconnectHandler;
import me.devnatan.socket4m.message.MessageHandler;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class Worker extends EventEmitter implements Runnable {

    @Getter private final Client client;
    @Getter @Setter private Socket socket;
    @Getter @Setter private boolean running = false;
    @Getter @Setter private MessageHandler messageHandler;
    @Getter @Setter private boolean online = false;

    Worker(Client client) {
        this.client = client;
    }

    Worker(Client client, Socket socket) {
        this.client = client;
        this.socket = socket;
    }

    public void run() {
        if(socket == null)
            throw new IllegalArgumentException("Socket must be defined");
        try {
            try {
                while (running && online) {
                    if(messageHandler != null) messageHandler.handle(client);
                }
            } catch (SocketTimeoutException e) {
                online = false;
                client.emit("error", new Arguments.Builder()
                        .with(Argument.of("throwable", e))
                        .with(Argument.of("reason", SocketCloseReason.TIMEOUT))
                        .build()
                );
            } catch (SocketException e) {
                online = false;
                client.emit("error", new Arguments.Builder()
                        .with(Argument.of("throwable", e))
                        .with(Argument.of("reason", SocketCloseReason.RESET))
                        .build()
                );
                client.handleIf(h -> h instanceof DefaultReconnectHandler);
            } catch (IOException e) {
                online = false;
                client.emit("error", new Arguments.Builder()
                        .with(Argument.of("throwable", e))
                        .with(Argument.of("reason", SocketCloseReason.IO))
                        .build()
                );
                running = false;
            } finally {
                online = false;
                running = false;
                socket.close();
                client.emit("disconnect");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void work() {
        running = true;
        client.emit("connect", new Arguments.Builder()
                .with(Argument.of("reason", null))
                .build());
        new Thread(this, "Client").start();
    }

}
