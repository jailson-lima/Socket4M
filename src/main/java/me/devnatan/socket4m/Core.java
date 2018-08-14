package me.devnatan.socket4m;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Core {

    private static Core instance;
    private Logger logger;
    private boolean debug;

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
        }

        System.out.println(message);
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public static Core getInstance() {
        return instance;
    }

    public void setInstance(Core instance) {
        if(instance != null)
            throw new IllegalArgumentException("Core is already set.");
        Core.instance = instance;
    }

}
