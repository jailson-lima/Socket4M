package me.devnatan.socket4m;

import com.google.gson.GsonBuilder;
import me.devnatan.socket4m.enums.SocketCloseReason;
import me.devnatan.socket4m.handler.def.DefaultReconnectHandler;
import me.devnatan.socket4m.message.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class TestClient {

    private TestClient() {
        Client client = new Client();
        client.setAddress("xxx.xx.xx.xx");
        client.setPort(8080);
        client.setDebug(true);
        client.addHandler(new DefaultReconnectHandler(client, 10));
        client.connect(socketOpenReason -> {
            Map<String, Object> map = new HashMap<>();
            map.put("key", "socket-client");
            map.put("value", "unknown");

            Message m = new Message();
            m.setValues(map);
            m.setText(new GsonBuilder().create().toJson(map, Map.class));

            if(client.write(m)) {
                client.debug(Level.INFO, "Message " + m.toJson() + " written to the server.");
            } else {
                client.debug(Level.SEVERE, "Failed to writte message to the server.");
            }
        });

        client.on("error", args -> {
            SocketCloseReason reason = (SocketCloseReason) args.value("reason");
            client.debug(Level.SEVERE, "Error: " + reason.name());
        });
    }

    public static void main(String[] args) {
        new TestClient();
    }

}
