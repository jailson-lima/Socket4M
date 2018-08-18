package me.devnatan.socket4m.client.enums;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public enum SocketCloseReason {

    /**
     * When the client tried to connect to the server or
     * an existing connection already exists but the response time is beyond that determined.
     * throw SocketTimeoutException
     */
    TIMEOUT,

    /**
     * When the client tries to create a connection with the server but it is not online.
     * throw ConnectException
     */
    REFUSED,

    /**
     * Called when there is a connection error.
     * throw IOException
     */
    IO,

    /**
     * Called when there is an open connection with the server,
     * and the server closes it probably unexpectedly.
     * throw SocketException
     */
    RESET;

    public static SocketCloseReason find(Throwable throwable) {
        if(throwable instanceof SocketTimeoutException)
            return TIMEOUT;

        if(throwable instanceof ConnectException)
            return REFUSED;

        if(throwable instanceof SocketException)
            return RESET;

        if(throwable instanceof IOException)
            return IO;

        return null;
    }

}
