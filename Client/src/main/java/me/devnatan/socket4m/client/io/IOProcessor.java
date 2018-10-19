package me.devnatan.socket4m.client.io;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.devnatan.socket4m.client.connection.Connection;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;

@AllArgsConstructor
public abstract class IOProcessor<T> {

    @Getter protected final Connection connection;
    @Getter protected final BlockingQueue<T> queue;
    @Getter protected final int buffer;

    /**
     * Process the content within the queue
     * @throws IOException
     *         If an I/O error occurs
     */
    public abstract void proccess() throws IOException, ExecutionException, InterruptedException;

}
