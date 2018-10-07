package me.devnatan.socket4m.handler;

import me.devnatan.socket4m.message.Message;

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

    protected abstract void onWrite(Message m);
    protected abstract void onRead(Message m);

}
