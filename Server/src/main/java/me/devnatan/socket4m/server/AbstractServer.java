package me.devnatan.socket4m.server;

import lombok.Data;
import me.devnatan.socket4m.server.connection.Connection;
import me.devnatan.socket4m.server.manager.ConnectionManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.util.Properties;

@Data
public abstract class AbstractServer implements Server {

    protected final Connection connection;
    protected final Logger logger = Logger.getLogger("LogM4");
    protected final ConnectionManager connectionManager = new ConnectionManager();
    protected boolean running;
    protected ServerHeart heart;

    protected AbstractServer(Connection connection) {
        this.connection = connection;
        Properties p = new Properties();
        p.setProperty("log4j.rootLogger", "INFO, stdout, LogM4");
        p.setProperty("log4j.rootCategory", "TRACE");
        p.setProperty("log4j.appender.stdout", "org.apache.log4j.ConsoleAppender");
        p.setProperty("log4j.appender.stdout.layout", "org.apache.log4j.PatternLayout");
        p.setProperty("log4j.appender.stdout.layout.ConversionPattern","[%d{MM/dd/yyyy HH:mm:ss}] [%4p] %m%n");
        p.setProperty("log4j.appender.LogM4", "org.apache.log4j.RollingFileAppender");
        p.setProperty("log4j.appender.LogM4.File", "server.log");
        p.setProperty("log4j.appender.LogM4.MaxFileSize", "100KB");
        p.setProperty("log4j.appender.LogM4.MaxBackupIndex", "1");
        p.setProperty("log4j.appender.LogM4.layout", "org.apache.log4j.PatternLayout");
        p.setProperty("log4j.appender.LogM4.layout.ConversionPattern", "[%d{MM/dd/yyyy HH:mm:ss}] [%4p] %m%n");
        PropertyConfigurator.configure(p);
    }

}
