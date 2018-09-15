package me.devnatan.socket4m;

import events4j.EventEmitter;
import events4j.argument.Argument;
import events4j.argument.Arguments;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.devnatan.socket4m.enums.SocketCloseReason;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;

@Data
@EqualsAndHashCode(callSuper = true)
public class Worker extends EventEmitter implements Runnable {

    private final Client client;
    private SocketChannel socket;
    private boolean running = false;
    private boolean online = false;

    Worker(Client client, SocketChannel socket) {
        this.client = client;
        this.socket = socket;
    }

    public void run() {
        try {
            do {
                client.getMessageHandler().handle(client);
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

    void work() {
        if(running) {
            client.debug(Level.WARNING, "Worker is already running.");
        } else {
            running = true;
            online = true;
            new Thread(this, "Client").start();
        }
    }

    void finish() {
        running = false;
        online = false;
        try {
            socket.close();
            client.emit("disconnect");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
