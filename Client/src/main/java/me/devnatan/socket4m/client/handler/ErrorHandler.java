package me.devnatan.socket4m.client.handler;

import me.devnatan.socket4m.client.Client;
import me.devnatan.socket4m.client.Worker;
import me.devnatan.socket4m.client.connection.Connection;
import me.devnatan.socket4m.client.io.IOProcessor;

import java.io.IOException;
import java.net.ConnectException;
import java.nio.channels.AlreadyConnectedException;
import java.nio.channels.ConnectionPendingException;

public abstract class ErrorHandler {

    public enum Error {

        UNKNOWN,

        CONNECT,

        ALREADY_CONNECTED,

        CONNECTION_PENDING,

        IO

    }

    public void handle(Throwable t) {
        Error r;
        if(t instanceof ConnectException)
            r = Error.CONNECT;
        else if(t instanceof AlreadyConnectedException)
            r = Error.ALREADY_CONNECTED;
        else if(t instanceof ConnectionPendingException)
            r = Error.CONNECTION_PENDING;
        else if(t instanceof IOException)
            r = Error.IO;
        else {
            r = Error.UNKNOWN;
            t.printStackTrace();
        }

        on(t, r);
    }

    /**
     * Called when there is an error in the {@link Connection}
     * execution or a {@link Worker} connected to it.
     * @see IOProcessor#proccess()
     * @see Worker#work()
     * @see Client#connect()
     * @param t = the exception
     * @param r = the error
     */
    public abstract void on(Throwable t, Error r);

}
