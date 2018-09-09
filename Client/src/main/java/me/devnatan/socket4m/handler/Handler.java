package me.devnatan.socket4m.handler;

import java.util.function.Consumer;

@FunctionalInterface
public interface Handler {

    void handle(Consumer<Boolean> complete);

}
