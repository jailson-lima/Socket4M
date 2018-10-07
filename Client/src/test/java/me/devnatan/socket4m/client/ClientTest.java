package me.devnatan.socket4m.client;

import me.devnatan.socket4m.client.connection.Connection;
import me.devnatan.socket4m.client.handler.ConnectionHandler;
import me.devnatan.socket4m.client.handler.ErrorHandler;
import me.devnatan.socket4m.client.handler.MessageHandler;
import me.devnatan.socket4m.client.io.Reader;
import me.devnatan.socket4m.client.io.Writer;
import me.devnatan.socket4m.client.message.Message;

import java.util.concurrent.LinkedBlockingQueue;

public class ClientTest {

    private static Client client;

    public static void main(String[] args){
        client = new Client();
        Connection c = new Connection("localhost", 4434);
        ErrorHandler errorHandler = new ErrorHandler() {
            @Override
            protected void on(Throwable t, Error r) {
                if(c.isReconnectTrying() && r == Error.CONNECT) {
                    client.getLogger().error("Error because is trying to reconnect.");
                    return;
                }
                t.printStackTrace();
                client.getLogger().error(r.name() + ": " + t);
            }
        };
        c.setErrorHandler(errorHandler);
        MessageHandler messageHandler = new MessageHandler() {
            @Override
            protected void onWrite(Message m) {

            }

            @Override
            protected void onRead(Message m) {
                client.getLogger().info("Message: " + m.toJson());
            }
        };
        c.setMessageHandler(messageHandler);
        ConnectionHandler connectionHandler = new ConnectionHandler() {
            @Override
            public void onConnect(Connection c) {
                client.getLogger().info("Connected successfully.");
            }

            @Override
            public void onDisconnect(Connection c) {
                client.getLogger().info("Disconnected successfully.");
            }

            @Override
            public void onFailConnect(Connection c) {
                client.getLogger().warn("Couldn't connect to the server.");
            }

            @Override
            public void onReconnect(Connection c) {
                client.getLogger().info("Reconnected successfully.");
            }

            @Override
            public void onTryConnect(Connection c) { }

        };
        c.setConnectionHandler(connectionHandler);
        Worker w = new Worker();
        w.setClient(client);
        w.setReader(new Reader(c, new LinkedBlockingQueue<>(), 1024));
        w.setWriter(new Writer(c, new LinkedBlockingQueue<>(), 1024));
        client.setConnection(c);
        client.setWorker(w);
        client.connect();
    }

}
