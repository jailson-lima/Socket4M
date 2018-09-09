package me.devnatan.socket4m.enums;

public enum SocketOpenReason {

    CONNECT,

    /**
     * When a connection is already open it is reopened with the server through a re-connection.
     */
    RECONNECT

}
