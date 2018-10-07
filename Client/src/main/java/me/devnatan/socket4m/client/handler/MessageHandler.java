package me.devnatan.socket4m.client.handler;

import me.devnatan.socket4m.client.message.Message;

public abstract class MessageHandler {

    public void handle(String s, Message m) {
        switch (s) {
            case "read":
                onRead(m);
                break;
            case "write":
                onWrite(m);
                break;
        }
    }

    /**
     * When you send a message to the server.
     * @param m = the message
     */
    protected abstract void onWrite(Message m);

    /**
     * When you receive a message from the server.
     * @param m = the message
     */
    protected abstract void onRead(Message m);

}
