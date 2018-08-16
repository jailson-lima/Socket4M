package me.devnatan.socket4m;

import me.devnatan.socket4m.client.Client;
import me.devnatan.socket4m.enums.SocketCloseReason;
import me.devnatan.socket4m.handler.def.DefaultReconnectHandler;
import me.devnatan.socket4m.message.Message;
import me.devnatan.socket4m.message.MessageHandler;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;

public class Example {

    private static void v3(String[] args) {
        // CORE
        Core core = new Core();
        core.setDebug(true);
        core.setMessageHandler(new MessageHandler(new ArrayBlockingQueue<>(100)));
        Core.setInstance(core);

        // CLIENT
        Client client = new Client();
        client.addOption("KEEP_ALIVE", true);
        client.addHandler(new DefaultReconnectHandler(client, client.getWorker(), 3));
        client.setAddress("localhost");
        client.setPort(4434);

        client.on("connect", arguments -> {
            core.log(Level.INFO, "Connected successfully.");
        });

        client.on("disconnect", arguments -> {

        });

        client.on("message", arguments -> {
            Message message = (Message) arguments.get("data").getValue();
            core.log(Level.INFO, "Message from the server:");
            core.log(Level.INFO, "  - Text: " + message.getText());
            core.log(Level.INFO, "  - Map: " + message.getValues());
            core.log(Level.INFO, "  - JSON: " + message.json());
        });

        client.on("error", arguments -> {
            Throwable throwable = (Throwable) arguments.get("throwable").getValue();
            SocketCloseReason reason = (SocketCloseReason) arguments.get("reason").getValue();
            if(reason == SocketCloseReason.RESET) {
                core.log(Level.SEVERE, "Server connection closed, trying to re-connect [" + throwable.getClass().getSimpleName() + "]...");
                return;
            }

            if(reason == SocketCloseReason.REFUSED) {
                core.log(Level.SEVERE, "Cannot connect to the server.");
                return;
            }

            throwable.printStackTrace();
        });

        client.connect();
    }

    public static void main(String[] args) {
        v3(args);
    }
}
