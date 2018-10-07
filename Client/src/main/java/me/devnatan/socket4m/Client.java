package me.devnatan.socket4m;

import lombok.Data;
import me.devnatan.socket4m.connection.Connection;
import me.devnatan.socket4m.message.Message;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.util.Properties;

@Data
public class Client {

    public Client(){
        Properties properties=new Properties();
        properties.setProperty("log4j.rootLogger","INFO, stdout,LogM4");
        properties.setProperty("log4j.rootCategory","TRACE");
        properties.setProperty("log4j.appender.stdout",     "org.apache.log4j.ConsoleAppender");
        properties.setProperty("log4j.appender.stdout.layout",  "org.apache.log4j.PatternLayout");
        properties.setProperty("log4j.appender.stdout.layout.ConversionPattern","[%d{yyyy/MM/dd HH:mm:ss}] [%5p] %m%n");
        properties.setProperty("log4j.appender.LogM4", "org.apache.log4j.RollingFileAppender");
        properties.setProperty("log4j.appender.LogM4.File", "client.log");
        properties.setProperty("log4j.appender.LogM4.MaxFileSize", "100KB");
        properties.setProperty("log4j.appender.LogM4.MaxBackupIndex", "1");
        properties.setProperty("log4j.appender.LogM4.layout",  "org.apache.log4j.PatternLayout");
        properties.setProperty("log4j.appender.LogM4.layout.ConversionPattern","[%d{yyyy/MM/dd HH:mm:ss}] [%5p] %m%n");
        PropertyConfigurator.configure(properties);
    }

    private Connection connection;
    private Worker worker;
    private Logger log = Logger.getLogger("LogM4");

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

