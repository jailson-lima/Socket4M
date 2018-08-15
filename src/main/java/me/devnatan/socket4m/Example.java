package me.devnatan.socket4m;

import me.devnatan.socket4m.client.Client;
import me.devnatan.socket4m.client.message.Message;
import me.devnatan.socket4m.client.message.MessageHandler;

import java.util.concurrent.ArrayBlockingQueue;

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
        client.setAddress("localhost");
        client.setPort(8080);
        client.setTimeout(5000);

        client.on("connect", arguments -> {

        });

        client.on("disconnect", arguments -> {

        });

        client.on("message", arguments -> {
            Message message = (Message) arguments.get("data").getValue();
        });

        client.on("error", arguments -> {
            Throwable throwable = (Throwable) arguments.get("throwable").getValue();

            throwable.printStackTrace();
        });

        client.connect();
    }

    public static void main(String[] args) {
        v3(args);
    }
}
