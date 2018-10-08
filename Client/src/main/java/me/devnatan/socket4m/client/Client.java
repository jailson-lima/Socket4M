package me.devnatan.socket4m.client;

import lombok.Data;
import me.devnatan.socket4m.client.connection.Connection;
import me.devnatan.socket4m.client.message.Message;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.util.Properties;

@Data
public class Client {

    private Connection connection;
    private Worker worker;
    private Logger logger = Logger.getLogger("LogM4");
    private boolean autoReconnect;

    public Client() {
        Properties p = new Properties();
        p.setProperty("log4j.rootLogger", "INFO, stdout, LogM4");
        p.setProperty("log4j.rootCategory", "TRACE");
        p.setProperty("log4j.appender.stdout", "org.apache.log4j.ConsoleAppender");
        p.setProperty("log4j.appender.stdout.layout", "org.apache.log4j.PatternLayout");
        p.setProperty("log4j.appender.stdout.layout.ConversionPattern","[%d{yyyy/MM/dd HH:mm:ss}] [%4p] %m%n");
        p.setProperty("log4j.appender.LogM4", "org.apache.log4j.RollingFileAppender");
        p.setProperty("log4j.appender.LogM4.File", "client.log");
        p.setProperty("log4j.appender.LogM4.MaxFileSize", "100KB");
        p.setProperty("log4j.appender.LogM4.MaxBackupIndex", "1");
        p.setProperty("log4j.appender.LogM4.layout", "org.apache.log4j.PatternLayout");
        p.setProperty("log4j.appender.LogM4.layout.ConversionPattern", "[%d{yyyy/MM/dd HH:mm:ss}] [%4p] %m%n");
        PropertyConfigurator.configure(p);
    }

    /**
     * @see Connection#connect()
     * @return boolean
     */
    public boolean connect() {
        if(connection.connect()) {
            logger.info("Connected successfully.");
            worker.work();
            return true;
        } logger.warn("Failed to connect.");
        return false;
    }

    /**
     * @see Connection#reconnect()
     * @return boolean
     */
    public boolean reconnect() {
        if(connection.reconnect()) {
            logger.info("Reconnected successfully.");
            return true;
        } else logger.warn("Failed to reconnect.");
        return false;
    }

    /**
     * @see Connection#disconnect(boolean)
     * @return boolean
     */
    public boolean disconnect() {
        if(connection.disconnect(false)) {
            logger.info("Disconnected successfully.");
            return true;
        } else
            logger.error("Failed to disconnect.");
        return false;
    }

    /**
     * Adds a message in the message queue to be sent to the server.
     * If the message is null it will not be sent.
     * @param message = string object
     */
    public void send(Message message) {
        assert message != null;
        if(worker.isRunning() &&
                connection.getChannel().isOpen() &&
                connection.getChannel().isConnected())
            worker.getWriter().getQueue().add(message);
    }

    /**
     * @see #send(Message)
     */
    public void send(String text) {
        send(Message.builder().with("text", text).build());
    }

}

