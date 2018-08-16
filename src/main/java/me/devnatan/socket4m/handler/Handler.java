package me.devnatan.socket4m.handler;

import me.devnatan.socket4m.enums.SocketCloseReason;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@FunctionalInterface
public interface Handler<T> {

    void handle(Consumer<T> success, BiConsumer<Throwable, SocketCloseReason> failed);

    default void handle() {
        handle((t) -> {}, (a, b) -> {});
    }

}
