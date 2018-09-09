package me.devnatan.socket4m.handler;

import lombok.Getter;
import me.devnatan.socket4m.Client;
import me.devnatan.socket4m.Worker;

public abstract class AbstractHandler implements Handler {

    @Getter protected final Client client;
    @Getter protected final Worker worker;
    @Getter protected boolean complete = false;

    public AbstractHandler(Client client, Worker worker) {
        this.client = client;
        this.worker = worker;
    }

}
