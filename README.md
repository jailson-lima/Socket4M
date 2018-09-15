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
This client supports some options that the [Socket] class (https://docs.oracle.com/javase/8/docs/api/java/net/Socket.html) contains.
```java
// socket.setKeepAlive(true);
client.addOption("KEEP_ALIVE", true);

// socket.setOOBInline(true);
client.addOption("OUT_OF_BAND_DATA", true);
```

### Connecting
Assign an IP address or port only.
You can also assign address and port directly in the class.

** As of version 1.0.1 the mandatory use of the port has been removed. **
```java
client.setAddress("127.0.0.1");
client.setPort(8080);
```

Or assign only at the time of connection.
```java
client.connect("127.0.0.1", 8080);
```

Assigning `TIMEOUT` to the connection.
```java
// 10 segundos
client.setTimeout(10000);

// or
client.connect("127.0.0.1", 8080, 10000);
```

Then
```java
// from 1.0.1
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
// Address and / or port are already assigned in the class.
client.connect();
```
**NOTE: Before making a connection, make sure you have defined the events previously.**

## Events
### Syntax
```
client.on(event, args -> {
  // ...
});
```

### When to disconnect
This event has no arguments.
```java
client.on("disconnect", args -> {
  // ...
});
```

### Message
Receive from the server:
```java
client.on("message", args -> {
  Message message = (Message) args.value("message");
  client.log(Level.INFO, "Message received from the server: " + message.toJson());
});
```

Sent to the server:
```java
// You can use multiple keys and values directly in the constructor without having to create a new map, useful for small messages.
Message m = new Message<>("one", 1, "two", 2, "three", 3); // Message<String, Integer> or simply Map<String, Object>

/* 
  to return the json content of the message
  for example: {"content":{"key":"socket-client", "value":"unknown"}}
 */
String json = m.toJson();

// sending the message
client.write(m);
```

### Handling connection errors
```java
client.on("error", args -> {
  Throwable throwable = (Throwable) arguments.get("throwable").getValue();
  SocketCloseReason reason = (SocketCloseReason) arguments.get("reason").getValue();
  if(reason == SocketCloseReason.RESET) {
    // Called regardless of whether there is a reconnection handler.
    client.log(Level.SEVERE, "Connecting to the closed server, trying to reconnect...");
    return;
  }

  if(reason == SocketCloseReason.REFUSED) {
    client.log(Level.SEVERE, "Could not connect to server.");
    return;
  }

  ((Throwable) arguments.get("throwable").getValue()).printStackTrace();
});
```
  
## Needing help?
  - Make a issue.
  - Our site [MotoNetwork](https://motocrack.net)
