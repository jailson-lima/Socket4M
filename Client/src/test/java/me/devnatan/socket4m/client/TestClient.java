package me.devnatan.socket4m.client;

import me.devnatan.socket4m.client.connection.Connection;
import me.devnatan.socket4m.client.handler.ConnectionHandler;
import me.devnatan.socket4m.client.handler.ErrorHandler;
import me.devnatan.socket4m.client.handler.MessageHandler;
import me.devnatan.socket4m.client.io.Reader;
import me.devnatan.socket4m.client.io.Writer;
import me.devnatan.socket4m.client.message.Message;

import java.util.concurrent.LinkedBlockingQueue;

public class TestClient {

    private static Client client = new Client();

    public static void main(String[] args) {
        // CONNECTION
        Connection c = new Connection("localhost", 4444);
        c.setTimeout(3000);

        // HANDLERS
        c.setErrorHandler(new MyErrorHandler());
        c.setMessageHandler(new MyMessageHandler());
        c.setConnectionHandler(new MyConnectionHandler());

        // WORKER
        Worker w = new Worker();
        w.setClient(client);
        w.setReader(new Reader(c, new LinkedBlockingQueue<>(), 1024));
        w.setWriter(new Writer(c, new LinkedBlockingQueue<>(), 1024));

        // SETUP
        client.setConnection(c);
        client.setWorker(w);
        client.setAutoReconnect(false);
        client.connect();
    }

    static class MyErrorHandler extends ErrorHandler {

        public void on(Throwable t, Error r) {
            client.getLogger().error(r.name() + ": " + t);
        }

    }

    static class MyMessageHandler extends MessageHandler {

        public void onWrite(Message m) {
            client.getLogger().info("Sent " + m.toJson() + " to the server.");
        }

        public void onRead(Message m) {
            client.getLogger().info("Received " + m.toJson() + " from the server.");
        }
    }

    static class MyConnectionHandler extends ConnectionHandler {

        public void onConnect(Connection c) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            client.send("Hello server!");
        }

        public void onDisconnect(Connection c) {
            client.getLogger().info("Disconnected successfully.");
        }

        public void onFailConnect(Connection c) {
            client.getLogger().warn("Couldn't connect to the server.");
        }

        public void onReconnect(Connection c) {
            client.getLogger().info("Reconnected successfully.");
        }

        // It's a loop.
        public void onTryConnect(Connection c) { }
    }

}
