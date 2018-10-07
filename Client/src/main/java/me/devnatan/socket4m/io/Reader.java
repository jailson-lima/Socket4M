package me.devnatan.socket4m.io;

import me.devnatan.socket4m.connection.Connection;
import me.devnatan.socket4m.message.Message;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.concurrent.BlockingQueue;

public class Reader extends IOProcessor<Message> {

    public Reader(Connection connection, BlockingQueue<Message> queue, int buffer) {
        super(connection, queue, buffer);
    }

    @Override
    public void proccess() throws IOException {
        ByteBuffer bb = ByteBuffer.allocate(buffer);
        StringBuilder sb = new StringBuilder();
        while ((connection.getChannel().read(bb)) > 0) {
            bb.flip();
            sb.append(Charset.defaultCharset().decode(bb));
        }

        if (sb.toString().length() > 0) {
            Message m = Message.fromJson(sb.toString());
            queue.add(m);
            if(connection.getMessageHandler() != null)
                connection.getMessageHandler().handle("read", m);
        }
    }

}
