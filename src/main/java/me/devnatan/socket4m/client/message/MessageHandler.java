package me.devnatan.socket4m.client.message;

import me.devnatan.socket4m.client.Client;

import java.io.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static me.devnatan.socket4m.util.DebugUtil.debug;

public class MessageHandler {

    public void handle(Client client) throws IOException {
        read(client.getWorker().getSocket().getInputStream(), (message) -> {
            debug("Reading message from server: " + message);
        });
    }

    public void write(Message message, OutputStream out, Runnable before, BiConsumer<Message, Long> complete) throws IOException {
        long time = System.currentTimeMillis();
        before.run();
        OutputStreamWriter osw = new OutputStreamWriter(out, "UTF-8");
        osw.write(message.to());
        osw.flush();
        complete.accept(message, System.currentTimeMillis() - time);
    }

    public static void read(InputStream in, Consumer<Message> consumer) throws IOException {
        char[] buffer = new char[2048];
        int charsRead = 0;
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        while ((charsRead = br.read(buffer)) != -1) {
            String message = new String(buffer).substring(0, charsRead);
            consumer.accept(Message.from(message));
        }
    }

}
