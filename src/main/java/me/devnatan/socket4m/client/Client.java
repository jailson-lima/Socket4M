package me.devnatan.socket4m.client;

import it.shadow.events4j.EventEmitter;
import it.shadow.events4j.argument.Argument;
import it.shadow.events4j.argument.Arguments;
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

    private String address;
    private int port;
    private int timeout = -1;
    private Worker worker = new Worker(this);
    private final Map<String, Object> options = new HashMap<>();
    private final List<Handler> handlers = new LinkedList<>();
    private Utilities utilities;
    private MessageHandler messageHandler;

    /**
     * Address of the server to connect to
     * @return String
     */
    public String getAddress() {
        return address;
    }

    /**
     * Set the address of the server to connect to
     * @param address = the server address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Port of the server to connect to
     * @return int
     */
    public int getPort() {
        return port;
    }

    /**
     * Set the port of the server to connect to
     * @param port = the server port
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Minimum server response time
     * @return int
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * Sets the minimum server response time
     * @param timeout = minimum response time
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * Responsible for receiving messages from the server.
     * @return Worker
     */
    public Worker getWorker() {
        return worker;
    }

    /**
     * Set the responsible for receiving messages from the server.
     * @param worker = the worker
     */
    public void setWorker(Worker worker) {
        this.worker = worker;
    }

    /**
     * Get socket channel connection map options.
     * @return Map
     */
    public Map<String, Object> getOptions() {
        return options;
    }

    /**
     * Add an option to the socket channel connection options map.
     * @param option = option key
     * @param value = option object value
     */
    public void addOption(String option, Object value) {
        options.put(option, value);
    }

    /**
     * Manipulators intended for a specific method or function and called when necessary.
     * @return List
     */
    public List<Handler> getHandlers() {
        return handlers;
    }

    /**
     * Adds a standard handler or one that implements {@link Handler} the list of handlers.
     * @param handler = handler
     * @return boolean
     */
    public boolean addHandler(Handler handler) {
        return handlers.add(handler);
    }

    public void handleIf(Predicate<Handler> predicte) {
        handlers.stream().filter(predicte).findFirst().ifPresent(Handler::handle);
    }

    public Utilities getUtilities() {
        return utilities;
    }

    public void setUtilities(Utilities utilities) {
        this.utilities = utilities;
        messageHandler = utilities.getMessageHandler();
    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public void setMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
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
            long now = System.currentTimeMillis();
            Socket socket = new Socket(address, port);
            if (timeout != -1)
                socket.setSoTimeout(timeout);
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
            worker.work(now);
        } catch (ConnectException e) {
            emit("error", new Arguments.Builder()
                    .addArgument(Argument.of("throwable", e))
                    .addArgument(Argument.of("reason", SocketCloseReason.REFUSED))
                    .build()
            );
        } catch (IOException e) {
            emit("error", new Arguments.Builder()
                    .addArgument(Argument.of("throwable", e))
                    .addArgument(Argument.of("reason", SocketCloseReason.IO))
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
     */
    public void write(Message message) {
        this.worker.getSend().add(message);
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
