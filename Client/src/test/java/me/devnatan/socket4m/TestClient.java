package me.devnatan.socket4m;

import me.devnatan.socket4m.enums.SocketCloseReason;
import me.devnatan.socket4m.message.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class TestClient {

    private TestClient() {
        Client client = new Client();
        client.setAddress("localhost");
        client.setPort(4434);
        client.setTimeout(1000);
        client.setDebug(true);
        client.connect(socketOpenReason -> {
            Map<String, Object> map = new HashMap<>();
            map.put("key", "socket-client");
            map.put("value", "unknown");

            Message m = new Message(map);
            if(client.write(m)) {
                client.debug(Level.INFO, "Message " + m.toJson() + " written to the server.");
            } else {
                client.debug(Level.SEVERE, "Failed to writte message to the server.");
            }
        });

        client.on("error", args -> {
            SocketCloseReason reason = (SocketCloseReason) args.value("reason");
            Throwable cause = (Throwable) args.value("throwable");
            client.debug(Level.SEVERE, "Error: " + reason.name() + " -> " + cause.toString());
        });
    }

    public static void main(String[] args) {
        new TestClient();
    }

}
