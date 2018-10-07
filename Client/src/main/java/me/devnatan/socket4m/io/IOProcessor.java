package me.devnatan.socket4m.io;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.devnatan.socket4m.connection.Connection;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

@AllArgsConstructor
public abstract class IOProcessor<T> {

    @Getter protected final Connection connection;
    @Getter protected final BlockingQueue<T> queue;
    @Getter protected final int buffer;

    public abstract void proccess() throws IOException;

}
