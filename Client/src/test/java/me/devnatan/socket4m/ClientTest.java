package me.devnatan.socket4m;

import me.devnatan.socket4m.connection.Connection;
import me.devnatan.socket4m.handler.ConnectionHandler;
import me.devnatan.socket4m.handler.ErrorHandler;
import me.devnatan.socket4m.handler.MessageHandler;
import me.devnatan.socket4m.io.Reader;
import me.devnatan.socket4m.io.Writer;
import me.devnatan.socket4m.message.Message;

import java.util.concurrent.LinkedBlockingQueue;

public class ClientTest {

    private static Client client;

    public static void main(String[] args){
        client = new Client();
        Connection c = new Connection("localhost", 4434);
        ErrorHandler errorHandler = new ErrorHandler() {
            @Override
            protected void on(Throwable t, Error r) {

            }
        };
        c.setErrorHandler(errorHandler);
        MessageHandler messageHandler = new MessageHandler() {
            @Override
            protected void onWrite(Message m) {

            }

            @Override
            protected void onRead(Message m) {
                client.getLog().info("[Recive] - "+m.toString());
            }
        };
        c.setMessageHandler(messageHandler);
        ConnectionHandler connectionHandler = new ConnectionHandler() {
            @Override
            public void onConnect(Connection c) {
                client.getLog().info("Connected successfully.");
            }

            @Override
            public void onDisconnect(Connection c) {
                client.getLog().info("Disconnected successfully.");
            }

            @Override
            public void onFailConnect(Connection c) {
                client.getLog().info("could not connect.");
            }

            @Override
            public void onReconnect(Connection c) {
                client.getLog().info("Trying to reconnect.");
            }

            @Override
            public void onTryConnect(Connection c) {
                client.getLog().info("Try.");
            }

        };
        c.setConnectionHandler(connectionHandler);
        Worker w = new Worker();
        w.setErrorHandler(errorHandler);
        w.setReader(new Reader(c,new LinkedBlockingQueue<>(), 1024));
        w.setWriter(new Writer(c,new LinkedBlockingQueue<>(), 1024));
        client.setConnection(c);
        client.setWorker(w);
        client.connect();
    }

}
