package me.devnatan.socket4m;

import lombok.Data;
import me.devnatan.socket4m.connection.Connection;
import me.devnatan.socket4m.message.Message;

@Data
public class Client {

    private Connection connection;
    private Worker worker;

    public boolean connect() {
        if(connection.connect()) {
            worker.work();
            return true;
        } return false;
    }

    public boolean reconnect() {
        return connection.reconnect();
    }

    public boolean disconnect() {
        return connection.disconnect();
    }

    public void send(Message m) {
        worker.getWriter().getQueue().add(m);
    }

}

