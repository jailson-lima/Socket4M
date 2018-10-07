package me.devnatan.socket4m;

import lombok.Data;
import me.devnatan.socket4m.connection.Connection;
import me.devnatan.socket4m.message.Message;

@Data
public class Client {

    private Connection connection;
    private Worker worker;

    /**
     * @see Connection#connect()
     * @return boolean
     */
    public boolean connect() {
        if(connection.connect()) {
            worker.work();
            return true;
        } return false;
    }

    /**
     * @see Connection#reconnect()
     * @return boolean
     */
    public boolean reconnect() {
        return connection.reconnect();
    }

    /**
     * @see Connection#disconnect()
     * @return boolean
     */
    public boolean disconnect() {
        return connection.disconnect();
    }

    /**
     * Adds a message in the message queue to be sent to the server.
     * If the message is null it will not be sent.
     * @param m = message object
     */
    public void send(Message m) {
        worker.getWriter().getQueue().add(m);
    }

}

