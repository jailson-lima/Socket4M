package me.devnatan.socket4m.server;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.devnatan.socket4m.server.connection.ClientConnection;
import me.devnatan.socket4m.server.connection.Connection;
import me.devnatan.socket4m.server.manager.ConnectionManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

@Data
public abstract class AbstractServer implements Server {

    protected final Connection connection;
    protected final Logger logger = Logger.getLogger("LogM4");
    protected final ConnectionManager connectionManager
            = new ConnectionManager();

    public AbstractServer(Connection connection) {
        this.connection = connection;
        Properties p = new Properties();
        p.setProperty("log4j.rootLogger", "INFO, stdout, LogM4");
        p.setProperty("log4j.rootCategory", "TRACE");
        p.setProperty("log4j.appender.stdout", "org.apache.log4j.ConsoleAppender");
        p.setProperty("log4j.appender.stdout.layout", "org.apache.log4j.PatternLayout");
        p.setProperty("log4j.appender.stdout.layout.ConversionPattern","[%d{yyyy/MM/dd HH:mm:ss}] [%4p] %m%n");
        p.setProperty("log4j.appender.LogM4", "org.apache.log4j.RollingFileAppender");
        p.setProperty("log4j.appender.LogM4.File", "client.log");
        p.setProperty("log4j.appender.LogM4.MaxFileSize", "100KB");
        p.setProperty("log4j.appender.LogM4.MaxBackupIndex", "1");
        p.setProperty("log4j.appender.LogM4.layout", "org.apache.log4j.PatternLayout");
        p.setProperty("log4j.appender.LogM4.layout.ConversionPattern", "[%d{yyyy/MM/dd HH:mm:ss}] [%4p] %m%n");
        PropertyConfigurator.configure(p);
    }

    @Override
    public boolean start() {
        try {
            Selector s = Selector.open();
            getConnection().setChannel(ServerSocketChannel.open());
            ServerSocketChannel ssc = (ServerSocketChannel) getConnection().getChannel();

            ssc.bind(getConnection().getSocketAddress());
            ssc.configureBlocking(false);
            ssc.register(s, SelectionKey.OP_ACCEPT);

            new Thread(new Heart(s, this)).start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean stop() {
        ServerSocketChannel ssc = (ServerSocketChannel) getConnection().getChannel();
        if(ssc != null && ssc.isOpen()) {
            try {
                ssc.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    @AllArgsConstructor
    public class Heart implements Runnable {

        private final Selector selector;
        private final Server server;

        public void run() {
            ByteBuffer bb = ByteBuffer.allocate(256);

            try {
                while (server.getConnection().getChannel().isOpen()) {
                    selector.select();
                    Set<SelectionKey> sks = selector.selectedKeys();
                    Iterator<SelectionKey> iter = sks.iterator();
                    while (iter.hasNext()) {
                        SelectionKey sk = iter.next();

                        if (sk.isAcceptable()) {
                           accept(selector, (ServerSocketChannel) server.getConnection().getChannel());
                        }

                        if (sk.isReadable()) {
                            SocketChannel sc = (SocketChannel) sk.channel();
                            try {
                                read(bb, sk);
                            } catch (IOException e) {
                                ClientConnection c = (ClientConnection) getConnectionManager().detach(sc);
                                if(c != null)
                                    getLogger().info("[-] id.: " + c.getId() + " - " + c.getSocketAddress() + " disconnected.");
                                else
                                    getLogger().warn("[x] Failed to detach " + sc.getRemoteAddress() + ".");
                                sc.close();
                                break;
                            }
                        }

                        iter.remove();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void read(ByteBuffer bb, SelectionKey sk) throws IOException {
            SocketChannel sc = (SocketChannel) sk.channel();
            sc.read(bb);

            // String m = new String(bb.array()).trim();
            // sc.close();

            bb.flip();
            sc.write(bb);
            bb.clear();
        }

        private void accept(Selector s, ServerSocketChannel ssc) throws IOException {
            SocketChannel sc = ssc.accept();
            sc.configureBlocking(false);
            sc.register(s, SelectionKey.OP_READ);

            ClientConnection c = (ClientConnection) connectionManager.attach(sc);
            getLogger().info("[+] id.: " + c.getId() + " - " + c.getSocketAddress() + " connected.");
        }

    }

}
