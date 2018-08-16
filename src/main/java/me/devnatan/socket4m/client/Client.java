package me.devnatan.socket4m.client;

import it.shadow.events4j.EventEmitter;
import it.shadow.events4j.argument.Argument;
import it.shadow.events4j.argument.Arguments;
import lombok.Getter;
import lombok.Setter;
import me.devnatan.socket4m.enums.SocketCloseReason;
import me.devnatan.socket4m.handler.Handler;
import me.devnatan.socket4m.message.Message;
import me.devnatan.socket4m.message.MessageHandler;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.logging.Level;

public class Client extends EventEmitter {

    @Getter @Setter private String address;
    @Getter @Setter private int port;
    @Getter @Setter private int timeout;
    @Getter @Setter private Worker worker;
    @Getter private final Map<String, Object> options;
    @Getter private final List<Handler> handlers;
    @Getter @Setter private Utilities utilities;
    @Getter @Setter private MessageHandler messageHandler;

    public Client() {
        timeout = -1;
        worker = new Worker(this);
        options = new HashMap<>();
        handlers = new LinkedList<>();
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
     * @return "true" if the handler is added; "false" if that handler already exists in the list.
     */
    public boolean addHandler(Handler handler) {
        return handlers.add(handler);
    }

    /**
     * Calls a handler under a condition
     * @param predicate = the condition
     */
    public void handleIf(Predicate<Handler> predicate) {
        handlers.stream().filter(predicate).findFirst().ifPresent(Handler::handle);
    }

    public void log(Level level, String message) {
        if(utilities != null)
            utilities.log(level, message);
    }

    /**
     * Connect to the server
     */
    public void connect() {
        if(address == null)
            throw new IllegalArgumentException("Server address cannot be null");
        if(port == -1)
            throw new IllegalArgumentException("Server port must be defined");

        try {
            Socket socket = new Socket(address, port);

            if(messageHandler == null) messageHandler = utilities.getMessageHandler();
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

            worker = new Worker(this, socket);
            worker.setMessageHandler(messageHandler);
            worker.setOnline(true);
            worker.work();
        } catch (ConnectException e) {
            emit("error", new Arguments.Builder()
                    .with(Argument.of("throwable", e))
                    .with(Argument.of("reason", SocketCloseReason.REFUSED))
                    .build()
            );
        } catch (IOException e) {
            emit("error", new Arguments.Builder()
                    .with(Argument.of("throwable", e))
                    .with(Argument.of("reason", SocketCloseReason.IO))
                    .build()
            );
        }
    }

    /**
     * Connect to the server
     * @param address = server address
     * @param port = server port
     */
    public void connect(String address, int port) throws IOException {
        this.address = address;
        this.port = port;
        this.timeout = -1;
        connect();
    }

    /**
     * Connect to the server
     * @param address = server address
     * @param port = server port
     * @param timeout = connection timeout
     */
    public void connect(String address, int port, int timeout) throws IOException {
        this.address = address;
        this.port = port;
        this.timeout = timeout;
        connect();
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
        if(worker == null || !worker.isRunning())
            throw new IllegalStateException("Socket client worker isn't running.");

        worker.setRunning(false);
        worker.once("end", arguments -> emit("disconnect"));
    }

}
