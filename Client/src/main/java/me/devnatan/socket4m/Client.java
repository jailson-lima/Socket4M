package me.devnatan.socket4m;

import lombok.Data;
import me.devnatan.socket4m.connection.Connection;
import me.devnatan.socket4m.message.Message;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.util.Properties;

@Data
public class Client {

    private Connection connection;
    private Worker worker;
    private Logger logger = Logger.getLogger("LogM4");

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
            worker.work();
            return true;
        } return false;
    }

    /**
     * @see Connection#reconnect()
     * @return boolean
     */
    public boolean reconnect() {
        return connection.reconnect();
    }

    /**
     * @see Connection#disconnect()
     * @return boolean
     */
    public boolean disconnect() {
        return connection.disconnect();
    }

    /**
     * Adds a message in the message queue to be sent to the server.
     * If the message is null it will not be sent.
     * @param m = message object
     */
    public void send(Message m) {
        worker.getWriter().getQueue().add(m);
    }

}

