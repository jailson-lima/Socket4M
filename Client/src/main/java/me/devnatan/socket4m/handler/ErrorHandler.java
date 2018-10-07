package me.devnatan.socket4m.handler;

import me.devnatan.socket4m.Client;
import me.devnatan.socket4m.Worker;
import me.devnatan.socket4m.io.IOProcessor;

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
     * Called when there is an error in the {@link me.devnatan.socket4m.connection.Connection}
     * execution or a {@link me.devnatan.socket4m.Worker} connected to it.
     * @see IOProcessor#proccess()
     * @see Worker#work()
     * @see Client#connect()
     * @param t = the exception
     * @param r = the error
     */
    protected abstract void on(Throwable t, Error r);

}
