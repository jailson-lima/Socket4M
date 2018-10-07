package me.devnatan.socket4m.handler;

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
        Error r = Error.UNKNOWN;
        if(t instanceof ConnectException)
            r = Error.CONNECT;
        else if(t instanceof AlreadyConnectedException)
            r = Error.ALREADY_CONNECTED;
        else if(t instanceof ConnectionPendingException)
            r = Error.CONNECTION_PENDING;
        else if(t instanceof IOException)
            r = Error.IO;

        on(t, r);
    }

    protected abstract void on(Throwable t, Error r);

}
