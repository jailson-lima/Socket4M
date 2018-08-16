package me.devnatan.socket4m.message;

import it.shadow.events4j.argument.Argument;
import it.shadow.events4j.argument.Arguments;
import me.devnatan.socket4m.client.Client;

import java.io.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class MessageHandler {

    private BlockingQueue<Message> readQueue;
    private BlockingQueue<Message> writeQueue;
    private int buffer = 2048;

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

    public BlockingQueue<Message> getReadQueue() {
        return readQueue;
    }

    public BlockingQueue<Message> getWriteQueue() {
        return writeQueue;
    }

    public void setReadQueue(BlockingQueue<Message> readQueue) {
        this.readQueue = readQueue;
    }

    public void setWriteQueue(BlockingQueue<Message> writeQueue) {
        this.writeQueue = writeQueue;
    }

    public int getBuffer() {
        return buffer;
    }

    public void setBuffer(int buffer) {
        this.buffer = buffer;
    }

    public void handle(Client client) throws IOException {
        if(!writeQueue.isEmpty())
            write(writeQueue.poll(), client.getWorker().getSocket().getOutputStream());
        read(client.getWorker().getSocket().getInputStream(), (message) -> process(message, client));
    }

    private void process(Message message, Client client) {
        readQueue.offer(message);
        client.emit("message", new Arguments.Builder()
                .addArgument(Argument.of("data", message))
                .build()
        );
    }

    private void write(Message message, OutputStream out) throws IOException {
        OutputStreamWriter osw = new OutputStreamWriter(out, "UTF-8");
        osw.write(message.to());
        osw.flush();
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
