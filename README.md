# Socket4M
Client socket for interaction with a TCP protocol server.

### Maven
```xml
<repositories>
    <repository>
        <id>socket4m-repo</id>
        <url>http://motocrack.net</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>me.devnatan.socket4m</groupId>
        <artifactId>Client</artifactId>
        <version>2.0.2</version>
    </dependency>
</dependencies>
```

### Information
  - Learn more about issuing events in [Events4J](https://github.com/theShadow89/Events4J).
  - Read about [Socket](https://docs.oracle.com/javase/8/docs/api/java/net/Socket.html).
  - Read about [SocketChannel](https://docs.oracle.com/javase/8/docs/api/java/nio/channels/SocketChannel.html).
  - **Java versions smaller than 8 are not supported.**
 
  
# Examples

### Client
```java
Client client = new Client();

// Enable the "debug" option to see details on the console.
client.setDebug(true);

// Set a "Logger" by default to be used in the debug.
// if not contains, set to null.
client.setLogger(yourLogger);
```

#### Client options
This client supports some options that the [Socket](https://docs.oracle.com/javase/8/docs/api/java/net/Socket.html) class contains.
```java
// socket.setKeepAlive(true);
client.addOption("KEEP_ALIVE", true);

// socket.setOOBInline(true);
client.addOption("OUT_OF_BAND_DATA", true);
```

### Connecting
Assign an IP address or port only.
You can also assign address and port directly in the class.
```java
client.setAddress("127.0.0.1");
client.setPort(8080);
```

Or assign only at the time of connection.
```java
client.connect("127.0.0.1", 8080);
```

Assigning **TIMEOUT** to the connection.
```java
// 10 seconds
client.setTimeout(10000);

// or
client.connect("127.0.0.1", 8080, 10000);
```

Then connect.
```java
client.connect(8080, reason -> {
  if(reason == SocketOpenReason.CONNECT) {
    client.log(Level.INFO, "First connected successfully.");
  }

  if(reason == SocketOpenReason.RECONNECT) {
    client.log(Level.INFO, "Reconnected successfully.");
  }
});
```

Or, you can connect by assigning address in the class and not at the time of connection.
```java
// Address and or port are already assigned in the class.
client.connect();
```
**NOTE: Before making a connection, make sure you have defined the events previously.**

To finish the connection
```java
client.disconnect();
```

## Events
### Syntax
```
client.on(event, args -> {
  // ...
});
```

### Error
```java
client.on("error", args -> {
  SocketCloseReason reason = (SocketCloseReason) arguments.value("reason");
  if(reason == SocketCloseReason.RESET) {
    client.log(Level.SEVERE, "Connecting to the closed server. Try to reconnect!");
    return;
  }

  if(reason == SocketCloseReason.REFUSED) {
    client.log(Level.SEVERE, "Could not connect to server.");
    return;
  }

  ((Throwable) arguments.value("throwable")).printStackTrace();
});
```

### Message received
```java
client.on("message", args -> {
  Message message = (Message) args.value("message");
  client.log(Level.INFO, "Message received from the server: " + message.toJson());
});
```

## Message
Declaring
```java
/*
    You can use multiple keys and values directly in the constructor without having to create a new map, useful for small messages.
    The message object is immutable.
 */
new Message<>(map);
new Message<>(k, v);
new Message<>(k1, v1, k2, v2);
new Message<>(k1, v1, k2, v2, k3, v3);
new Message<>(k1, v1, k2, v2, k3, v3, k4, v4);
new Message<>(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5);
```

The message
```java
/* 
    to return the json content of the message
    for example: {"content":{"key":"socket-client", "value":"unknown"}}
 */
String json = message.toJson();

// sending the message
client.write(message);
```
  
## Needing help?
  - Make a issue.
  - Our site [MotoNetwork](https://motocrack.net)
