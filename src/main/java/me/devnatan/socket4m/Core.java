package me.devnatan.socket4m;

import me.devnatan.socket4m.message.MessageHandler;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Core {

    private static Core instance;
    private Logger logger;
    private boolean debug;
    private MessageHandler messageHandler;

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

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

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public void setMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    public static Core getInstance() {
        return instance;
    }

    public static void setInstance(Core instance) {
        if(Core.instance != null)
            throw new IllegalArgumentException("Core is already set.");
        Core.instance = instance;
    }

}
