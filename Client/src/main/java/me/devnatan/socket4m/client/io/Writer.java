package me.devnatan.socket4m.client.io;

import me.devnatan.socket4m.client.connection.Connection;
import me.devnatan.socket4m.client.message.Message;

import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;

public class Writer extends IOProcessor<Message> {

    public Writer(Connection connection, BlockingQueue<Message> queue, int buffer) {
        super(connection, queue, buffer);
    }

    /**
     * Writes a message contained in the message queue to be written to the server,
     * to the server. This message can not be null.
     * If a message handler exists, it handles the message.
     */
    @Override
    public void proccess() {
        if(!queue.isEmpty()) {
            Message m = queue.poll();
            byte[] b = m.toJson().getBytes();
            ByteBuffer bb = ByteBuffer.wrap(b);
            try {
                if(connection.getChannel().write(bb).get() != -1) {
                    assert connection.getMessageHandler() != null;
                    connection.getMessageHandler().handle("write", m);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            bb.clear();
        }
    }

}
