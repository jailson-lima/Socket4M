package me.devnatan.socket4m.client;

import it.shadow.events4j.EventEmitter;
import it.shadow.events4j.argument.Argument;
import it.shadow.events4j.argument.Arguments;
import me.devnatan.socket4m.enums.SocketCloseReason;
import me.devnatan.socket4m.enums.SocketOpenReason;
import me.devnatan.socket4m.handler.def.DefaultReconnectHandler;
import me.devnatan.socket4m.message.Message;
import me.devnatan.socket4m.message.MessageHandler;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

public class Worker extends EventEmitter implements Runnable {

    private final Client client;
    private Socket socket;
    private final List<Message> send = new ArrayList<>();
    private boolean running = false;
    private final MessageHandler messageHandler = new MessageHandler();
    private boolean online = false;

    Worker(Client client) {
        this.client = client;
    }

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

    public void setSocket(Socket socket) {
        this.socket = socket;
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

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public void run() {
        if(socket == null)
            throw new IllegalArgumentException("Socket must be defined");
        try {
            try {
                while (running && online) {
                    messageHandler.handle(client);
                }
            } catch (SocketTimeoutException e) {
                online = false;
                client.emit("error", new Arguments.Builder()
                        .addArgument(Argument.of("throwable", e))
                        .addArgument(Argument.of("reason", SocketCloseReason.TIMEOUT))
                        .build()
                );
            } catch (SocketException e) {
                online = false;
                client.emit("error", new Arguments.Builder()
                        .addArgument(Argument.of("throwable", e))
                        .addArgument(Argument.of("reason", SocketCloseReason.RESET))
                        .build()
                );
                client.handleIf(h -> h instanceof DefaultReconnectHandler);
            } catch (IOException e) {
                online = false;
                client.emit("error", new Arguments.Builder()
                        .addArgument(Argument.of("throwable", e))
                        .addArgument(Argument.of("reason", SocketCloseReason.IO))
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

    public void work(long time) {
        running = true;
        client.emit("connect", new Arguments.Builder()
                .addArgument(Argument.of("reason", SocketOpenReason.UNDEFINED))
                .build());
        new Thread(this, "Client").start();
    }

}
