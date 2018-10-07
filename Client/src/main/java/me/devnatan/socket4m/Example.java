package me.devnatan.socket4m;

import me.devnatan.socket4m.connection.Connection;
import me.devnatan.socket4m.handler.ConnectionHandler;
import me.devnatan.socket4m.handler.ErrorHandler;
import me.devnatan.socket4m.handler.MessageHandler;
import me.devnatan.socket4m.io.Reader;
import me.devnatan.socket4m.io.Writer;
import me.devnatan.socket4m.message.Message;

import java.util.concurrent.LinkedBlockingQueue;

public class Example {

    private Client client;

    private Example() {
        // SETUP CONNECTION
        Connection c = new Connection("localhost", 4434);
        c.setConnectionHandler(handleMyConnections());
        c.setMessageHandler(handleMyMessages());
        c.setErrorHandler(handleMyErrors());

        // SETUP WORKER
        Worker w = new Worker();
        w.setReader(new Reader(c, new LinkedBlockingQueue<>(), 1024));
        w.setWriter(new Writer(c, new LinkedBlockingQueue<>(), 1024));
        w.setErrorHandler(c.getErrorHandler());

        // FINISH SETUP
        client = new Client();
        client.setConnection(c);
        client.setWorker(w);
    }

    private MessageHandler handleMyMessages() {
        return new MessageHandler() {
            protected void onWrite(Message m) {
                System.out.println("Message sent to the server: " + m.toJson());
            }

            protected void onRead(Message m) {
                System.out.println("Message received from the server: " + m.toJson());
            }
        };
    }

    private ConnectionHandler handleMyConnections() {
        return new ConnectionHandler() {
            public void onConnect(Connection c) {
                System.out.println("Connected successfully!");
            }

            public void onDisconnect(Connection c) {
                System.out.println("Disconnected successfully!");
            }

            public void onFailConnect(Connection c) {
                System.out.println("Failed to connect to the server.");
            }

            public void onReconnect(Connection c) { }
            public void onTryConnect(Connection c) { }
        };
    }

    private ErrorHandler handleMyErrors() {
        return new ErrorHandler() {
            @Override
            protected void on(Throwable t, Error r) {
                System.err.println("Error [" + r.name() + "]: " + t.toString());
            }
        };
    }

    public static void main(String[] args) {
        Example e = new Example();
        e.client.connect();
    }

}
