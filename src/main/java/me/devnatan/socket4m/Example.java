package me.devnatan.socket4m;

import me.devnatan.socket4m.client.Client;
import me.devnatan.socket4m.client.Utilities;
import me.devnatan.socket4m.enums.SocketCloseReason;
import me.devnatan.socket4m.handler.def.DefaultReconnectHandler;
import me.devnatan.socket4m.message.Message;
import me.devnatan.socket4m.message.MessageHandler;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;

public class Example {

    private static void v3(String[] args) {
        // UTILITIES
        Utilities utilities = new Utilities();
        utilities.setDebug(true);
        utilities.setMessageHandler(new MessageHandler(new ArrayBlockingQueue<>(100)));

        // CLIENT
        Client client = new Client();
        client.setUtilities(utilities);
        client.addOption("KEEP_ALIVE", true);
        client.addHandler(new DefaultReconnectHandler(client, client.getWorker(), 3));
        client.setAddress("localhost");
        client.setPort(4434);

        // EVENTS
        client.on("disconnect", arguments -> {
            client.log(Level.INFO, "Disconnected from the server.");
        });
        client.on("message", arguments -> {
            Message message = (Message) arguments.value("data");
            client.log(Level.INFO, "Message from the server:");
            client.log(Level.INFO, "  - Text: " + message.getText());
            client.log(Level.INFO, "  - Map: " + message.getValues());
            client.log(Level.INFO, "  - JSON: " + message.json());
        });

        client.on("error", arguments -> {
            Throwable throwable = (Throwable) arguments.value("throwable");
            SocketCloseReason reason = (SocketCloseReason) arguments.value("reason");
            if(reason == SocketCloseReason.RESET) {
                client.log(Level.SEVERE, "Server connection closed, trying to re-connect [" + throwable.getClass().getSimpleName() + "]...");
                return;
            }

            if(reason == SocketCloseReason.REFUSED) {
                client.log(Level.SEVERE, "Cannot connect to the server.");
                return;
            }

            throwable.printStackTrace();
        });

        // CONNECT TO THE SERVER
        client.connect();
    }

    public static void main(String[] args) {
        v3(args);
    }
}
