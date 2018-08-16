package me.devnatan.socket4m.client;

import lombok.Getter;
import lombok.Setter;
import me.devnatan.socket4m.message.MessageHandler;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Utilities {

    @Getter @Setter private Logger logger;
    @Getter @Setter private boolean debug;
    @Getter @Setter private MessageHandler messageHandler;

    public void log(Level level, String message) {
        if(logger != null) {
            logger.log(level, message);
            return;
        }

        if(level == Level.SEVERE) {
            System.err.println(message);
            return;
        }

        System.out.println(message);
    }

}
