package me.devnatan.socket4m.server.data;

import lombok.Data;

import java.net.Socket;
import java.nio.ByteBuffer;

@Data
public abstract class Client implements Runnable {
    public abstract int getId();
    public abstract Socket getSocket();
    public abstract void sendMessage(String message) throws Throwable;
    public abstract void reciveMessage() throws Throwable;
    public abstract void run();
}
