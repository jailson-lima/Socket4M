package me.devnatan.socket4m.client.handler.def;

import lombok.Getter;
import lombok.Setter;
import me.devnatan.socket4m.client.Client;
import me.devnatan.socket4m.client.enums.SocketOpenReason;
import me.devnatan.socket4m.client.handler.AbstractHandler;

import java.util.function.Consumer;
import java.util.logging.Level;

public class DefaultReconnectHandler extends AbstractHandler {

    @Getter @Setter private int tries;
    @Getter @Setter private int attempts = 0;

    public DefaultReconnectHandler(Client client) {
        super(client, client.getWorker());
        this.tries = 10;
    }

    public DefaultReconnectHandler(Client client, int tries) {
        super(client, client.getWorker());
        this.tries = tries;
    }

    @Override
    public void handle(Consumer<Boolean> complete) {
        attempts = 0;
        this.complete = false;
        client.debug(Level.INFO, "Trying to reconnect...");
        reconnect(tries, complete);
    }

    private void reconnect(int tries, Consumer<Boolean> complete) {
        for(attempts = 0; attempts < (tries < 0 ? 1 : tries); attempts++) {
            if(this.complete) break;
            reconnect0(complete);
        }
    }

    /**
     * Wormhole
     *
     * @see #reconnect(int, Consumer)
     * @param complete = when complete
     */
    private void reconnect0(Consumer<Boolean> complete) {
        client.connect(consumer -> {
            if (consumer == SocketOpenReason.RECONNECT) {
                this.complete = true;
                complete.accept(true);
                return;
            }

            complete.accept(false);
        });
    }

}
