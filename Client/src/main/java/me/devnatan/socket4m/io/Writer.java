package me.devnatan.socket4m.io;

import me.devnatan.socket4m.connection.Connection;
import me.devnatan.socket4m.message.Message;

import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.concurrent.BlockingQueue;

public class Writer extends IOProcessor<Message> {

    public Writer(Connection connection, BlockingQueue<Message> queue, int buffer) {
        super(connection, queue, buffer);
    }

    /**
     * Writes a message contained in the message queue to be written to the server,
     * to the server. This message can not be null.
     * If a message handler exists, it handles the message.
     * @throws IOException
     *         If an I/O error occurs
     */
    @Override
    public void proccess() throws IOException {
        if(!queue.isEmpty()) {
            Message m = queue.poll();
            CharBuffer buffer = CharBuffer.wrap(m.toJson());
            while (buffer.hasRemaining()) {
                connection.getChannel().write(Charset.defaultCharset().encode(buffer));
            }

            assert connection.getMessageHandler() != null;
            connection.getMessageHandler().handle("write", queue.poll());
        }
    }

}
