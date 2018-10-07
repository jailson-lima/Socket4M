package me.devnatan.socket4m;

import lombok.Data;
import me.devnatan.socket4m.handler.ErrorHandler;
import me.devnatan.socket4m.io.Reader;
import me.devnatan.socket4m.io.Writer;

import java.io.IOException;

@Data
public class Worker implements Runnable {

    private boolean running;
    private Reader reader;
    private Writer writer;
    private ErrorHandler errorHandler;

    @Override
    public void run() {
        try {
            while(running) {
                writer.proccess();
                reader.proccess();
            }
        } catch (IOException e) {
            if(errorHandler != null)
                errorHandler.handle(e);
        } finally {
            running = false;
        }
    }

    public boolean work() {
        if(running)
            throw new IllegalStateException("Worker is already running.");
        else {
            new Thread(this, "Socket4M-Client").start();
            running = true;
            return true;
        }
    }

}
