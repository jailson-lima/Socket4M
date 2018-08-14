package me.devnatan.socket4m.client;

import it.shadow.events4j.EventEmitter;
import me.devnatan.socket4m.client.message.Message;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketOption;
import java.net.StandardSocketOptions;
import java.util.HashMap;
import java.util.Map;

public class Client extends EventEmitter {

    private String address;
    private int port;
    private int timeout = -1;
    private Worker worker;
    private final Map<SocketOption, Object> options = new HashMap<>();
    private boolean canDebug = true;
    private boolean canPrintErrors = true;

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
    public Map<SocketOption, Object> getOptions() {
        return options;
    }

    /**
     * If you can see debug messages on console.
     * @return
     */
    public boolean isCanDebug() {
        return canDebug;
    }

    /**
     * Set if you can see debug messages on console.
     * @param canDebug = if you can see
     */
    public void setCanDebug(boolean canDebug) {
        this.canDebug = canDebug;
    }

    /**
     * If you can see stack trace messages on console.
     * @return boolean
     */
    public boolean isCanPrintErrors() {
        return canPrintErrors;
    }

    /**
     * Set if you can see stack trace messages on console.
     * @param canPrintErrors = if you can see
     */
    public void setCanPrintErrors(boolean canPrintErrors) {
        this.canPrintErrors = canPrintErrors;
    }

    /**
     * Add an option to the socket channel connection options map.
     * @param option = the {@link StandardSocketOptions} option
     * @param value = option object value
     */
    public void addOption(SocketOption option, Object value) {
        options.put(option, value);
    }

    /**
     * Connect to the server
     */
    public void connect() throws IOException {
        if(address == null)
            throw new IllegalArgumentException("Server address cannot be null");
        if(port == -1)
            throw new IllegalArgumentException("Server port must be defined");

        long now = System.currentTimeMillis();
        Socket socket = new Socket(address, port);
        if (timeout != -1)
            socket.setSoTimeout(timeout);
        if (options.size() > 0) {
            /* options.forEach((so, v) -> {
                if (!so.name().equals("SO_TIMEOUT")) {
                    try {
                        socket.getChannel().setOption(so, v);
                    } catch (IOException e) {
                        if (canDebug)
                            e.printStackTrace();
                        emit("error", new Arguments.Builder()
                                .addArgument(Argument.of("throwable", e))
                                .addArgument(Argument.of("reason", SocketCloseReason.IO))
                                .build()
                        );
                    }
                }
            }); */
        }
        worker = new Worker(this, socket);
        worker.work(now);
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
