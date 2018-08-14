package me.devnatan.socket4m.client.message;

import me.devnatan.socket4m.client.Client;

import java.io.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static me.devnatan.socket4m.util.DebugUtil.debug;

public class MessageHandler {

    public void handle(Client client) throws IOException {
        read(client.getWorker().getSocket().getInputStream(), (message) -> {});
    }

    private void write(Message message, OutputStream out, BiConsumer<Message, Long> complete) throws IOException {
        long t = System.currentTimeMillis();
        OutputStreamWriter osw = new OutputStreamWriter(out, "UTF-8");
        osw.write(message.to());
        osw.flush();
        complete.accept(message, System.currentTimeMillis() - t);
    }

    private void read(InputStream in, Consumer<Message> consumer) throws IOException {
        char[] b = new char[2048];
        int c;
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        while((c = br.read(b)) != -1) {
            consumer.accept(Message.from(new String(b).substring(0, c)));
        }
    }

}
