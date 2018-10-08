package me.devnatan.socket4m.server.connection;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.net.InetSocketAddress;

@Data
@EqualsAndHashCode(callSuper = true)
public class ClientConnection extends Connection {

    private final int id;

    public ClientConnection(int id, int port) {
        super(port);
        this.id = id;
    }

    public ClientConnection(int id, InetSocketAddress address) {
        this(id, address.getPort());
        this.setAddress(address.getHostName());
    }

}
