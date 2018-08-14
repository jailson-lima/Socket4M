package me.devnatan.socket4m.client.enums;

public enum SocketCloseReason {

    /**
     * When the client tried to connect to the server or
     * an existing connection already exists but the response time is beyond that determined.
     */
    TIMEOUT,

    /**
     * When the client tries to create a connection with the server but it is not online.
     */
    REFUSED,

    /**
     * Called when there is a connection error.
     * Exception: IOException
     */
    IO

}
