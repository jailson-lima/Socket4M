package me.devnatan.socket4m.server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.devnatan.socket4m.server.connection.ClientConnection;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

@AllArgsConstructor
public class ServerHeart implements ServerIO, Runnable {

    @Getter private final Selector selector;
    @Getter private final Server server;

    public void run() {
        ByteBuffer bb = ByteBuffer.allocate(256);

        try {
            boolean hello = false;
            while (server.isRunning()) {
                selector.select();
                if(!selector.isOpen())
                    break;

                Set<SelectionKey> sks = selector.selectedKeys();
                Iterator<SelectionKey> iter = sks.iterator();
                while (iter.hasNext()) {
                    SelectionKey sk = iter.next();

                    if (!sk.isValid()) {
                        continue;
                    }

                    if (sk.isAcceptable()) {
                        accept(selector, (ServerSocketChannel) server.getConnection().getChannel());
                    }

                    if (sk.isReadable()) {
                        try {
                            read(bb, sk);
                        } catch (IOException e) {
                            try {
                                sk.channel().close();
                                ClientConnection cc = (ClientConnection) server.getConnectionManager().detach((SocketChannel) sk.channel());
                                server.getLogger().info("[<] id.: " + cc.getId() + " - " + cc.getPrettyAddress() + " disconnected.");
                            } catch (IOException e1) {
                                e.printStackTrace();
                            }
                        }
                    }

                    if (sk.isWritable()) {
                        if(!hello) {
                            write(bb, sk);
                            hello = true;
                        }
                    }

                    iter.remove();
                }
            }

            server.getLogger().info("I/O connection closed.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String read(ByteBuffer bb, SelectionKey sk) throws IOException {
        String s = null;
        SocketChannel sc = (SocketChannel) sk.channel();
        sc.read(bb);

        String m = new String(bb.array()).trim();
        if(m.length() > 0) {
            s = m;
        }

        bb.clear();
        return s;
    }

    public void write(ByteBuffer bb, SelectionKey sk) throws IOException {
        SocketChannel sc = (SocketChannel) sk.channel();
        ByteBuffer buf = ByteBuffer.wrap("Olá cliente!".getBytes(StandardCharsets.UTF_8));
        sc.write(buf);
    }

    public void accept(Selector s, ServerSocketChannel ssc) throws IOException {
        SocketChannel sc = ssc.accept();
        sc.configureBlocking(false);
        sc.register(s, SelectionKey.OP_READ);
        sc.register(s, SelectionKey.OP_WRITE);

        ClientConnection c = (ClientConnection) server.getConnectionManager().attach(sc);
        server.getLogger().info("[>] id.: " + c.getId() + " - " + c.getPrettyAddress() + " connected.");
    }

}
