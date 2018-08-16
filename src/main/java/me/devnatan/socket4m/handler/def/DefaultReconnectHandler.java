package me.devnatan.socket4m.handler.def;

import lombok.Getter;
import lombok.Setter;
import me.devnatan.socket4m.client.Client;
import me.devnatan.socket4m.client.Worker;
import me.devnatan.socket4m.enums.SocketCloseReason;
import me.devnatan.socket4m.enums.SocketOpenReason;
import me.devnatan.socket4m.handler.AbstractHandler;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class DefaultReconnectHandler extends AbstractHandler<SocketOpenReason> {

    @Getter @Setter private int tries;
    @Getter @Setter private int attempts = 0;

    public DefaultReconnectHandler(Client client, Worker worker, int tries) {
        super(client, worker);
        this.tries = tries;
    }

    @Override
    public void handle(Consumer<SocketOpenReason> success, BiConsumer<Throwable, SocketCloseReason> failed) {
        reconnect(tries, success, failed);
    }

    private void reconnect(int tries, Consumer<SocketOpenReason> success, BiConsumer<Throwable, SocketCloseReason> failed) {
        if(worker == null)
            throw new NullPointerException("Worker cannot be null");

        if(worker.isOnline())
            throw new IllegalStateException("Worker is already online");

        if(tries > 0) {
            for(attempts = 0; attempts < tries; attempts++) {
                reconnect0(success, failed);
            }
        } else reconnect0(success, failed);
    }

    private void reconnect0(Consumer<SocketOpenReason> success, BiConsumer<Throwable, SocketCloseReason> failed) {
        if(worker.isOnline()) {
            success.accept(SocketOpenReason.RECONNECTED);
            attempts = 0;
            return;
        }

        try {
            client.connect();
        } catch (Exception e) {
            failed.accept(e.getCause(), SocketCloseReason.find(e.getCause()));
            attempts = 0;
        }
    }


}
