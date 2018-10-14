package me.devnatan.socket4m.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

public interface ServerIO {

    String read(ByteBuffer byteBuffer, SelectionKey selectionKey) throws IOException;

    void accept(Selector selector, ServerSocketChannel serverSocketChannel) throws IOException;

}
