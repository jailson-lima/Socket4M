package me.devnatan.socket4m.client.io;

import me.devnatan.socket4m.client.connection.Connection;
import me.devnatan.socket4m.client.message.Message;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;

public class Reader extends IOProcessor<Message> {

    public Reader(Connection connection, BlockingQueue<Message> queue, int buffer) {
        super(connection, queue, buffer);
    }

    /**
     * Reads the message contained within the {@link java.io.InputStream} of the connection channel.
     * A ByteBuffer allocates a specific amount of read capability, this capacity is given as {@link #buffer}.
     * If a message is found, it will be treated and added to the read queue.
     * If a message handler exists, it handles the message.
     * @throws IOException
     *         If an I/O error occurs
     */
    public void proccess() throws IOException {
        ByteBuffer bb = ByteBuffer.allocate(buffer);
        StringBuilder sb = new StringBuilder();

        while ((connection.getChannel().read(bb)) > 0) {
            bb.flip();
            sb.append(StandardCharsets.UTF_8.decode(bb));
        }
        String s = sb.toString().replace("\u0000\f", "");
        if (s.length() > 0) {
            Message m = Message.fromJson(s);
            queue.add(m);
            if(connection.getMessageHandler() != null)
                connection.getMessageHandler().handle("read", m);
        }
    }

}