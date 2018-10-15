package me.devnatan.socket4m.server.connection;

import lombok.Data;

import java.net.InetSocketAddress;
import java.nio.channels.NetworkChannel;

@Data
public class Connection {

    private final InetSocketAddress address;
    private NetworkChannel channel;

    public String address() {
        return address.getHostName() + ":" + address.getPort();
    }

}
