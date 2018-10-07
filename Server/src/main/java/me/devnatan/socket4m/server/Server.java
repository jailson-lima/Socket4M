package me.devnatan.socket4m.server;

import lombok.Data;
import me.devnatan.socket4m.server.data.Client;
import org.apache.log4j.Logger;
import java.util.Set;

@Data
public abstract class Server {

    public abstract String getName();

    public abstract String getVersion();

    public abstract Logger getLogger();

    public abstract Set<Client> getClients();

    public abstract Client getClient(int id);

    public abstract Client getClient(String ip);

    public abstract void kickClient(Client client);

    public abstract void kickClient(Client client, String reason);

    public abstract void kickIp(String ip);

    public abstract void kickIp(String ip, String reason);

    public abstract void start() throws Exception;

    public abstract void stop() throws Exception;

}
