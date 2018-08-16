package me.devnatan.socket4m.handler;

import me.devnatan.socket4m.client.Client;
import me.devnatan.socket4m.client.Worker;

public abstract class AbstractHandler<T> implements Handler<T> {

    protected final Client client;
    protected final Worker worker;

    public AbstractHandler(Client client, Worker worker) {
        this.client = client;
        this.worker = worker;
    }

}
