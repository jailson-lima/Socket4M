package me.devnatan.socket4m.client;

import it.shadow.events4j.EventEmitter;
import it.shadow.events4j.argument.Argument;
import it.shadow.events4j.argument.Arguments;
import lombok.Getter;
import lombok.Setter;
import me.devnatan.socket4m.client.enums.SocketCloseReason;
import me.devnatan.socket4m.client.handler.def.DefaultReconnectHandler;
import me.devnatan.socket4m.client.message.Message;
import me.devnatan.socket4m.client.message.MessageHandler;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.logging.Level;

public class Worker extends EventEmitter implements Runnable {

    @Getter private final Client client;
    @Getter @Setter private Socket socket;
    @Getter @Setter private MessageHandler messageHandler;
    @Getter @Setter private volatile boolean running = false;
    @Getter @Setter private volatile boolean online = false;

    Worker(Client client, Socket socket) {
        this.client = client;
        this.socket = socket;
    }

    public void run() {
        try {
            do {
                messageHandler.handle(client);
            } while(running);
        } catch (SocketTimeoutException e) {
            online = false;
            client.emit("error", new Arguments.Builder()
                    .with(Argument.of("throwable", e))
                    .with(Argument.of("reason", SocketCloseReason.TIMEOUT))
                    .build()
            );
        } catch (SocketException e) {
            online = false;
            client.handleIf(h -> h instanceof DefaultReconnectHandler);
            client.emit("error", new Arguments.Builder()
                    .with(Argument.of("throwable", e))
                    .with(Argument.of("reason", SocketCloseReason.RESET))
                    .build()
            );
        } catch (IOException e) {
            online = false;
            client.emit("error", new Arguments.Builder()
                    .with(Argument.of("throwable", e))
                    .with(Argument.of("reason", SocketCloseReason.IO))
                    .build()
            );
        } finally {
            finish();
        }
    }

    public void work() {
        if(running && client.getUtilities().isDebug()) {
            client.debug(Level.WARNING, "[DEBUG] Worker is already running.");
        } else if(!running) {
            messageHandler.on("message-read", args -> {
                client.debug(Level.INFO, "[DEBUG] Message received: " + ((Message) args.value("data")).getText());
            });

            messageHandler.on("message-write", args -> {
                client.debug(Level.INFO, "[DEBUG] Message write: " + ((Message) args.value("data")).getText());
            });
            online = true;
            new Thread(this, "Client").start();
        }
    }

    private void finish() {
        client.emit("disconnect");
        running = false;
        online = false;
    }

}
