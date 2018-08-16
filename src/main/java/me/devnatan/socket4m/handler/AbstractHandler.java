package me.devnatan.socket4m.handler;

import lombok.Getter;
import me.devnatan.socket4m.client.Client;
import me.devnatan.socket4m.client.Worker;

public abstract class AbstractHandler<T> implements Handler<T> {

    @Getter protected final Client client;
    @Getter protected final Worker worker;

    public AbstractHandler(Client client, Worker worker) {
        this.client = client;
        this.worker = worker;
    }

}
