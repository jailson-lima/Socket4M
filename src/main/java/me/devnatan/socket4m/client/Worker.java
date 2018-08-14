package me.devnatan.socket4m.client;

import it.shadow.events4j.EventEmitter;
import it.shadow.events4j.argument.Argument;
import it.shadow.events4j.argument.Arguments;
import me.devnatan.socket4m.client.enums.SocketCloseReason;
import me.devnatan.socket4m.client.message.Message;
import me.devnatan.socket4m.client.message.MessageHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import static me.devnatan.socket4m.util.DebugUtil.debug;

public class Worker extends EventEmitter implements Runnable {

    private final Client client;
    private final Socket socket;
    private final List<Message> send = new ArrayList<>();
    private boolean running = false;

    Worker(Client client, Socket socket) {
        this.client = client;
        this.socket = socket;
    }

    public Client getClient() {
        return client;
    }

    public Socket getSocket() {
        return socket;
    }

    public List<Message> getSend() {
        return send;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    private final MessageHandler messageHandler = new MessageHandler();

    public void run() {
        try {
            try {
                while (running) {
                    /* write(() -> {
                        debug("Writing message to the server...");
                    }, (message, time) -> {
                        debug("Message sent in " + (time / 1000) + "ms");
                    });

                    Message message = read();
                    if (message != null) {
                        debug("Reading message from server...");
                        client.emit("message", new Arguments.Builder()
                                .addArgument(Argument.of("data", message))
                                .build()
                        );
                    } */
                    messageHandler.handle(client);

                    if (client.isCanDebug()) {
                        debug("Running...");
                    }
                }

                // System.out.println("Stop");
            } catch (SocketTimeoutException e) {
                if (client.isCanPrintErrors())
                    e.printStackTrace();
                client.emit("error", new Arguments.Builder()
                        .addArgument(Argument.of("throwable", e))
                        .addArgument(Argument.of("reason", SocketCloseReason.TIMEOUT))
                        .build()
                );
            } catch (IOException e) {
                if (client.isCanPrintErrors())
                    e.printStackTrace();
                client.emit("error", new Arguments.Builder()
                        .addArgument(Argument.of("throwable", e))
                        .addArgument(Argument.of("reason", SocketCloseReason.IO))
                        .build()
                );
                running = false;
            } finally {
                socket.close();
                client.emit("disconnect");
                running = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void write(Runnable before, BiConsumer<Message, Long> complete) throws IOException {
        long time = System.currentTimeMillis();
        if(send.size() > 0) {
            before.run();
            Message sm = send.remove(0);
            OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream(), "UTF-8");
            osw.write(sm.to());
            osw.flush();
            complete.accept(sm, System.currentTimeMillis() - time);
        }
    }

    private Message read() throws IOException {
        Message message = null;
        InputStream inputStream = socket.getInputStream();

        byte[] tmp = new byte[1024];
        while(inputStream.available() > 0) {
            int i = inputStream.read(tmp, 0, 1024);
            if(i < 0)
                break;
            message = Message.from(new String(tmp, 0, i));
        }

        debug("Available: " + inputStream.available());

        try {
            Thread.sleep(1000);
        } catch(InterruptedException ignored) { }
        return message;
    }

    public void work(long time) {
        client.emit("connect", new Arguments.Builder()
                .addArgument(Argument.of("time", System.currentTimeMillis() - time))
                .build());
        debug("Working...");
        running = true;
        new Thread(this).start();
    }



}
