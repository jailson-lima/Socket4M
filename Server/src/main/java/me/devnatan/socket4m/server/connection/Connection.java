package me.devnatan.socket4m.server.connection;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.devnatan.socket4m.server.handler.IOHandler;

import java.net.InetSocketAddress;
import java.nio.channels.NetworkChannel;

@Data
public class Connection {

    private final InetSocketAddress address;
    private NetworkChannel channel;
    @EqualsAndHashCode.Exclude private IOHandler handler;

    public String address() {
        return address.getHostName() + ":" + address.getPort();
    }

}
