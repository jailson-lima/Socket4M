package me.devnatan.socket4m.client.handler;

import java.util.function.Consumer;

@FunctionalInterface
public interface Handler {

    void handle(Consumer<Boolean> complete);

}
