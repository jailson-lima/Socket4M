package me.devnatan.socket4m;

import lombok.Data;
import me.devnatan.socket4m.connection.Connection;

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

}

