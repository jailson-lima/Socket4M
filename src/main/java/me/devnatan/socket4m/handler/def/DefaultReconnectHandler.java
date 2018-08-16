package me.devnatan.socket4m.handler.def;

import me.devnatan.socket4m.Core;
import me.devnatan.socket4m.client.Client;
import me.devnatan.socket4m.client.Worker;
import me.devnatan.socket4m.enums.SocketCloseReason;
import me.devnatan.socket4m.enums.SocketOpenReason;
import me.devnatan.socket4m.handler.AbstractHandler;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Level;

public class DefaultReconnectHandler extends AbstractHandler<SocketOpenReason> {

    private int attempts = 0;

    public DefaultReconnectHandler(Client client, Worker worker) {
        super(client, worker);
    }

    public DefaultReconnectHandler(Client client, Worker worker, int attempts) {
        super(client, worker);
        this.attempts = attempts;
    }

    @Override
    public void handle(Consumer<SocketOpenReason> success, BiConsumer<Throwable, SocketCloseReason> failed) {
        reconnect(attempts, success, failed);
    }

    private void reconnect(int attempts, Consumer<SocketOpenReason> success, BiConsumer<Throwable, SocketCloseReason> failed) {
        if(worker == null)
            throw new NullPointerException("Worker cannot be null");

        /* if(!worker.isRunning())
            throw new IllegalStateException("Worker is already running"); */

        if(worker.isOnline())
            throw new IllegalStateException("Worker is already online");

        if(attempts > 0) {
            for(int i = 0; i < attempts; i++) {
                reconnect0(success, failed);
            }
        } else reconnect0(success, failed);
    }

    private void reconnect0(Consumer<SocketOpenReason> success, BiConsumer<Throwable, SocketCloseReason> failed) {
        if(worker.isOnline()) {
            success.accept(SocketOpenReason.RECONNECTED);
            return;
        }

        try {
            client.connect();
        } catch (Exception e) {
            failed.accept(e.getCause(), SocketCloseReason.find(e.getCause()));
        }
    }


}
