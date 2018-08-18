package me.devnatan.socket4m.client.message;

import events4j.EventEmitter;
import events4j.argument.Argument;
import events4j.argument.Arguments;
import lombok.Getter;
import lombok.Setter;
import me.devnatan.socket4m.client.Client;

import java.io.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Consumer;

public class MessageHandler extends EventEmitter {

    @Getter @Setter private BlockingQueue<Message> readQueue;
    @Getter @Setter private BlockingQueue<Message> writeQueue;
    @Getter @Setter private int buffer = 2048;

    public MessageHandler() {
        readQueue = new ArrayBlockingQueue<>(10);
        writeQueue = new LinkedBlockingDeque<>();
    }

    public MessageHandler(BlockingQueue<Message> readQueue) {
        this.readQueue = readQueue;
        writeQueue = new LinkedBlockingDeque<>();
    }

    public MessageHandler(BlockingQueue<Message> readQueue, int buffer) {
        this.readQueue = readQueue;
        this.buffer = buffer;
        writeQueue = new LinkedBlockingDeque<>();
    }

    public void handle(Client client) throws IOException {
        if(!writeQueue.isEmpty()) {
            write(writeQueue.poll(), client.getWorker().getSocket().getOutputStream());
        }

        read(client.getWorker().getSocket().getInputStream(), (message) -> process(message, client));
    }

    private void process(Message message, Client client) {
        if(readQueue.offer(message)) {
            Arguments args = new Arguments.Builder()
                    .with(Argument.of("data", message))
                    .build();
            client.emit("message", args);
            emit("message-read", args);
        }
    }

    private void write(Message message, OutputStream out) throws IOException {
        OutputStreamWriter osw = new OutputStreamWriter(out, "UTF-8");
        osw.write(message.json());
        osw.flush();
        emit("message-write", new Arguments.Builder()
                .with(Argument.of("data", message))
                .build());
    }

    private void read(InputStream in, Consumer<Message> consumer) throws IOException {
        char[] b = new char[buffer];
        int c;
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        while((c = br.read(b)) != -1) {
            consumer.accept(Message.from(new String(b).substring(0, c)));
        }
    }

}
