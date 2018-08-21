package me.devnatan.socket4m.client;

import me.devnatan.socket4m.client.enums.SocketCloseReason;
import me.devnatan.socket4m.client.handler.def.DefaultReconnectHandler;

import java.util.logging.Level;

public class TestClient {

    private TestClient() {
        Client client = new Client();
        client.setAddress("149.56.29.24");
        client.setPort(8083);
        client.setDebug(true);
        client.addHandler(new DefaultReconnectHandler(client, 10));
        client.connectNIO(socketOpenReason -> { });

        client.on("error", args -> {
            SocketCloseReason reason = (SocketCloseReason) args.value("reason");
            client.debug(Level.SEVERE, "Error: " + reason.name());
        });
    }

    public static void main(String[] args) {
        new TestClient();
    }

}
