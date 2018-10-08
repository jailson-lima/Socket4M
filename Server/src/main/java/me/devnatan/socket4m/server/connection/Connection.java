package me.devnatan.socket4m.server.connection;

import lombok.Data;

import java.net.InetSocketAddress;
import java.nio.channels.NetworkChannel;

@Data
public class Connection {

    private String address;
    private final int port;
    private NetworkChannel channel;

    public InetSocketAddress getSocketAddress() {
        if(address == null)
            return new InetSocketAddress(port);
        return new InetSocketAddress(address, port);
    }

}
