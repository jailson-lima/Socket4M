package me.devnatan.socket4m.client;

import it.shadow.events4j.EventEmitter;
import it.shadow.events4j.argument.Argument;
import it.shadow.events4j.argument.Arguments;
import me.devnatan.socket4m.client.enums.SocketCloseReason;
import me.devnatan.socket4m.client.message.Message;
import me.devnatan.socket4m.client.message.MessageHandler;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

public class Worker extends EventEmitter implements Runnable {

    private final Client client;
    private final Socket socket;
    private final List<Message> send = new ArrayList<>();
    private boolean running = false;

    Worker(Client client, Socket socket) {
        this.client = client;
        this.socket = socket;
    }

    public Client getClient() {
        return client;
    }

    public Socket getSocket() {
        return socket;
    }

    public List<Message> getSend() {
        return send;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    private final MessageHandler messageHandler = new MessageHandler();

    public void run() {
        try {
            try {
                while (running) {
                    messageHandler.handle(client);
                }
            } catch (SocketTimeoutException e) {
                client.emit("error", new Arguments.Builder()
                        .addArgument(Argument.of("throwable", e))
                        .addArgument(Argument.of("reason", SocketCloseReason.TIMEOUT))
                        .build()
                );
            } catch (IOException e) {
                client.emit("error", new Arguments.Builder()
                        .addArgument(Argument.of("throwable", e))
                        .addArgument(Argument.of("reason", SocketCloseReason.IO))
                        .build()
                );
                running = false;
            } finally {
                running = false;
                socket.close();
                client.emit("disconnect");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void work(long time) {
        running = true;
        client.emit("connect", new Arguments.Builder()
                .addArgument(Argument.of("time", System.currentTimeMillis() - time))
                .build());
        new Thread(this).start();
    }

}
