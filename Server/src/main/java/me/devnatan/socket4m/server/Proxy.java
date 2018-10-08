package me.devnatan.socket4m.server;

import me.devnatan.socket4m.server.data.Client;
import me.devnatan.socket4m.server.data.Message;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class Proxy extends Server implements Runnable{

    private Set<Client> clients;
    private boolean running;

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getVersion() {
        return null;
    }

    @Override
    public Logger getLogger() {
        return Logger.getLogger("LogM4");
    }

    @Override
    public Set<Client> getClients() {
        return clients;
    }

    @Override
    public Client getClient(int id) {
        for (Client client : clients){
            if(client.getId() == id){
                return client;
            }
        }
        return null;
    }

    @Override
    public Client getClient(String ip) {
        for (Client client : clients){
            if(client.getSocket().getInetAddress().getHostAddress() == ip){
                return client;
            }
        }
        return null;
    }

    @Override
    public void kickClient(Client client) {
        try {
            client.getSocket().getChannel().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void kickClient(Client client, String reason) {

    }

    @Override
    public void kickIp(String ip) {

    }

    @Override
    public void kickIp(String ip, String reason) {

    }

    @Override
    public void start() throws Exception {
        Properties p = new Properties();
        p.setProperty("log4j.rootLogger", "INFO, stdout, LogM4");
        p.setProperty("log4j.rootCategory", "TRACE");
        p.setProperty("log4j.appender.stdout", "org.apache.log4j.ConsoleAppender");
        p.setProperty("log4j.appender.stdout.layout", "org.apache.log4j.PatternLayout");
        p.setProperty("log4j.appender.stdout.layout.ConversionPattern","[%d{yyyy/MM/dd HH:mm:ss}] [%4p] %m%n");
        p.setProperty("log4j.appender.LogM4", "org.apache.log4j.RollingFileAppender");
        p.setProperty("log4j.appender.LogM4.File", "server.log");
        p.setProperty("log4j.appender.LogM4.MaxFileSize", "100KB");
        p.setProperty("log4j.appender.LogM4.MaxBackupIndex", "1");
        p.setProperty("log4j.appender.LogM4.layout", "org.apache.log4j.PatternLayout");
        p.setProperty("log4j.appender.LogM4.layout.ConversionPattern", "[%d{yyyy/MM/dd HH:mm:ss}] [%4p] %m%n");
        PropertyConfigurator.configure(p);
        clients = new HashSet<>();
        getLogger().info("Starting server...");
        if(running)
            throw new IllegalStateException("Server is already running.");
        else {
            new Thread(this, "Socket4M-Server").start();
        }
    }

    @Override
    public void stop() throws Exception {
        if(running){
            running = false;
            getLogger().info("Turning off the server");
        }else{
            throw new IllegalStateException("Server is not running.");
        }
    }

    @Override
    public void run() {
        running = true;

        try {
            ServerSocket serverSocket = new ServerSocket(4434);
            getLogger().info("Server started successfully");
            while(running) {
                Socket socket = serverSocket.accept();
                Client client = new Client() {
                    @Override
                    public int getId() {
                        return clients.size() + 1;
                    }

                    @Override
                    public Socket getSocket() {
                        return socket;
                    }

                    @Override
                    public SocketChannel getChannel() {
                        if(getSocket().getChannel() == null || !getSocket().getChannel().isOpen()) {
                            SocketChannel channel = null;
                            try {
                                channel = SocketChannel.open();
                                channel.configureBlocking(false);
                                return channel;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        return null;
                    }

                    @Override
                    public void sendMessage(String message) throws Throwable {
                        if(this.getSocket() != null){
                            DataOutputStream stream = new DataOutputStream(this.getSocket().getOutputStream());
                            stream.writeBytes(message);
                            stream.flush();
                            stream.close();
                        }
                    }

                    @Override
                    public void reciveMessage() throws Throwable {
                        if(this.getChannel().isConnected()){
                            ByteBuffer allocate = ByteBuffer.allocate(1024);
                            StringBuilder builder = new StringBuilder();
                            if(this.getChannel().read(allocate) > 0){
                                allocate.flip();
                                builder.append(StandardCharsets.UTF_8.decode(allocate));
                            }
                            String s = builder.toString().replace("\u0000\f", "");
                            if (s.length() > 0) {
                                Message m = Message.fromJson(s);
                                getLogger().info("[Client "+this.getId()+"] "+m.toJson());
                                m = null;
                            }
                            s =  null;
                            builder = null;
                            allocate = null;
                        }
                    }

                    @Override
                    public void run() {
                        try {
                            this.sendMessage("Welcome client (id "+this.getId()+")");
                            while (this.getChannel() != null){
                                if(this.getChannel().isOpen()){
                                    this.reciveMessage();
                                }
                            }
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    }
                };
                clients.add(client);
                new Thread(client).run();
                getLogger().info(clients.size());
                getLogger().info("Client connect (ID "+ client.getId()+" | IP "+client.getSocket().getInetAddress().getHostAddress()+")");
                //TO DO
            }
        } catch (Throwable e) {
            getLogger().warn("[ERROR] "+e.getMessage());
        }
    }
}
