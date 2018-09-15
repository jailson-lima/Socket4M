package me.devnatan.socket4m.message;

import events4j.argument.Argument;
import events4j.argument.Arguments;
import lombok.Data;
import me.devnatan.socket4m.Client;
import me.devnatan.socket4m.Worker;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

@Data
public class MessageHandler {

    private final Worker worker;
    private BlockingQueue<Message> readQueue;
    private BlockingQueue<Message> writeQueue;
    private int buffer = 2048;

    public MessageHandler(Worker worker) {
        readQueue = new ArrayBlockingQueue<>(100);
        writeQueue = new LinkedBlockingQueue<>();
        this.worker = worker;
    }

    public void handle(Client client) throws IOException {
        if(!writeQueue.isEmpty())
            write(writeQueue.poll());
        read((message) -> process(message, client));
    }

    private void process(Message message, Client client) {
        if(readQueue.offer(message)) {
            Arguments args = new Arguments.Builder()
                    .with(Argument.of("data", message))
                    .build();
            client.emit("message", args);
        }
    }

    private void write(Message message) throws IOException {
        CharBuffer buffer = CharBuffer.wrap(message.toJson());
        while (buffer.hasRemaining()) {
            worker.getSocket().write(Charset.defaultCharset().encode(buffer));
        }
    }

    private void read(Consumer<Message> consumer) throws IOException {
        ByteBuffer bufferA = ByteBuffer.allocate(20);
        StringBuilder message = new StringBuilder();
        SocketChannel channel = worker.getSocket();
        while ((channel.read(bufferA)) > 0) {
            bufferA.flip();
            message.append(Charset.defaultCharset().decode(bufferA));
        }

        if(message.toString().trim().length() > 0) {
            consumer.accept(Message.fromJson(message.toString()));
        }
    }

}
