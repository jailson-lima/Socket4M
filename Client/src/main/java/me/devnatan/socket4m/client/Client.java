package me.devnatan.socket4m.client;

import events4j.EventEmitter;
import events4j.argument.Argument;
import events4j.argument.Arguments;
import lombok.Getter;
import lombok.Setter;
import me.devnatan.socket4m.client.enums.SocketCloseReason;
import me.devnatan.socket4m.client.enums.SocketOpenReason;
import me.devnatan.socket4m.client.handler.Handler;
import me.devnatan.socket4m.client.message.Message;
import me.devnatan.socket4m.client.message.MessageHandler;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client extends EventEmitter {

    @Getter @Setter private String address;
    @Getter @Setter private int port;
    @Getter @Setter private int timeout;
    @Getter @Setter private Worker worker;
    @Getter private final Map<String, Object> options;
    @Getter private final List<Handler> handlers;
    @Getter @Setter private boolean debug;
    @Getter @Setter private Logger logger;
    @Getter @Setter private MessageHandler messageHandler;
    @Getter @Setter private boolean connected;

    public Client() {
        timeout = -1;
        options = new HashMap<>();
        handlers = new LinkedList<>();
        debug = false;
        connected = false;
    }

    /**
     * Adds a new option on the client socket.
     * @param option = the option key
     * @param value = the option value
     */
    public void addOption(String option, Object value) {
        options.put(option, value);
    }

    /**
     * Adds a new handler
     * @param handler = handler
     */
    public void addHandler(Handler handler) {
        handlers.add(handler);
    }

    /**
     * Calls a handler under a condition
     * @param predicate = the condition
     */
    public void handleIf(Predicate<Handler> predicate) {
        handlers.stream().filter(predicate).findFirst().ifPresent(h -> h.handle(c -> debug(c ? Level.INFO : Level.WARNING, "Handle " + h.getClass().getSimpleName() + " handle " + (c ? "success" : "failed") + ".")));
    }

    public void log(Level level, String message) {
        if(logger != null) {
            if (!logger.isLoggable(level)) {
                logger.log(Level.INFO, "[OFF] " + message);
                return;
            }

            logger.log(level, message);
            return;
        }

        if(level == Level.SEVERE) {
            System.err.println(message);
            return;
        }

        System.out.println(message);
    }

    public void debug(Level level, String message) {
        if(debug) log(level, "[DEBUG] " + message);
    }

    public void connectNIO(Consumer<SocketOpenReason> consumer) {
        if(port == -1)
            throw new IllegalArgumentException("Server port cannot be negative");

        try {
            SocketChannel socket = SocketChannel.open();
            socket.configureBlocking(false);

            if (timeout != -1) socket.socket().setSoTimeout(timeout);
            if (options.size() > 0) {
                for (Map.Entry<String, Object> entry : options.entrySet()) {
                    String k = entry.getKey();
                    Object v = entry.getValue();
                    switch (k) {
                        case "KEEP_ALIVE":
                            socket.socket().setKeepAlive((boolean) v);
                            break;
                        case "REUSE_ADDRESS":
                            socket.socket().setReuseAddress((boolean) v);
                            break;
                        case "TCP_NO_DELAY":
                            socket.socket().setTcpNoDelay((boolean) v);
                            break;
                        case "OUT_OF_BAND_DATA":
                            // IMPORTANT!
                            socket.socket().setOOBInline((boolean) v);
                            break;
                        case "WRITE_BUFFER_SIZE":
                            socket.socket().setSendBufferSize((int) v);
                            break;
                        case "READ_BUFFER_SIZE":
                            socket.socket().setReceiveBufferSize((int) v);
                            break;
                    }
                }
            }

            socket.connect(new InetSocketAddress(address, port));

            while (!socket.finishConnect()) {
                debug(Level.INFO, "Connecting...");
            }

            worker = new Worker(this, socket);
            messageHandler = new MessageHandler(worker);
            worker.work();

            if(connected && socket.isConnected()) {
                consumer.accept(SocketOpenReason.RECONNECT);
                debug(Level.INFO, "Reconnected successfully.");
                return;
            }

            connected = true;
            consumer.accept(SocketOpenReason.CONNECT);
            debug(Level.INFO, "Connected successfully.");
        } catch (ConnectException e) {
            emit("error", new Arguments.Builder()
                    .with(Argument.of("throwable", e))
                    .with(Argument.of("reason", SocketCloseReason.REFUSED))
                    .build()
            );
            debug(Level.SEVERE, "Connection refused.");
        } catch (IOException e) {
            emit("error", new Arguments.Builder()
                    .with(Argument.of("throwable", e))
                    .with(Argument.of("reason", SocketCloseReason.IO))
                    .build()
            );
            debug(Level.SEVERE, "I/O error: " + e.getMessage() + ".");
        }
    }

    /**
     * Connect to the server
     * @param consumer = when complete
     */
    public void connect(Consumer<SocketOpenReason> consumer) {
        if(port == -1)
            throw new IllegalArgumentException("Server port cannot be negative");

        if (messageHandler == null) {
            throw new IllegalArgumentException("Message handler cannot be null!");
        }

        try {
            Socket socket = new Socket(address, port);
            if (timeout != -1) socket.setSoTimeout(timeout);
            if (options.size() > 0) {
                for (Map.Entry<String, Object> entry : options.entrySet()) {
                    String k = entry.getKey();
                    Object v = entry.getValue();
                    switch (k) {
                        case "KEEP_ALIVE":
                            socket.setKeepAlive((boolean) v);
                            break;
                        case "REUSE_ADDRESS":
                            socket.setReuseAddress((boolean) v);
                            break;
                        case "TCP_NO_DELAY":
                            socket.setTcpNoDelay((boolean) v);
                            break;
                        case "OUT_OF_BAND_DATA":
                            // IMPORTANT!
                            socket.setOOBInline((boolean) v);
                            break;
                        case "WRITE_BUFFER_SIZE":
                            socket.setSendBufferSize((int) v);
                            break;
                        case "READ_BUFFER_SIZE":
                            socket.setReceiveBufferSize((int) v);
                            break;
                    }
                }
            }

            // TODO: Remake
            worker = new Worker(this, null);
            worker.work();

            if(connected && socket.isConnected()) {
                consumer.accept(SocketOpenReason.RECONNECT);
                debug(Level.INFO, "Reconnected successfully.");
                return;
            }

            connected = true;
            consumer.accept(SocketOpenReason.CONNECT);
            debug(Level.INFO, "Connected successfully.");
        } catch (ConnectException e) {
            emit("error", new Arguments.Builder()
                    .with(Argument.of("throwable", e))
                    .with(Argument.of("reason", SocketCloseReason.REFUSED))
                    .build()
            );
            debug(Level.SEVERE, "Connection refused.");
        } catch (IOException e) {
            emit("error", new Arguments.Builder()
                    .with(Argument.of("throwable", e))
                    .with(Argument.of("reason", SocketCloseReason.IO))
                    .build()
            );
            debug(Level.SEVERE, "I/O error: " + e.getMessage() + ".");
        }
    }

    /**
     * Sends a message to the server.
     * This message is an object that is received by the server in JSON format.
     * @param message = the message object (implements Map)
     * @return "true" if the message have been added to queue.
     */
    public boolean write(Message message) {
        return messageHandler.getWriteQueue().add(message);
    }

    /**
     * Terminates the connection to the server if it is connected and running.
     */
    public void end() {
        worker.finish();
        debug(Level.INFO, "Disconnected successfully.");
    }

}
