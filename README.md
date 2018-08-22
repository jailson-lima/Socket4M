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
  - Read about [SocketServer](https://docs.oracle.com/javase/8/docs/api/java/net/ServerSocket.html).
  - **Java versions smaller than 8 are not supported.**
  
### Asynchronism and parallel processing
Not yet implemented.
  
# Examples

### Client
```java
Client client = new Client();

// Enable the "debug" option to see details on the console.
utilities.setDebug(true);

// Set a "Logger" by default to be used in the debug.
utilities.setLogger(yourLogger);
```

#### Clinet options
This client supports some options that the [Socket] class (https://docs.oracle.com/javase/8/docs/api/java/net/Socket.html) contains.
```java
// socket.setKeepAlive(true);
client.addOption("KEEP_ALIVE", true);

// socket.setOOBInline(true);
client.addOption("OUT_OF_BAND_DATA", true);
```

### Handlers
There are still not many handlers available, only one.\
The `DefaultReconnectHandler` can be used for connections that require support for automatic reclosure.\
It is called when the server stops responding to the client or closes the connection unexpectedly.

```java
// 3 is the number of connection attempts
client.addHandler(new DefaultReconnectHandler(client, client.getWorker(), 3));

// you can also try to reconnect only once without assigning any value
client.addHandler(new DefaultReconnectHandler(client, client.getWorker()));

// or by assigning a value less than or equal to zero.
client.addHandler(new DefaultReconnectHandler(client, client.getWorker(), 0));

// from version 1.0.1 assigning a "Worker" is no longer required in the constructor.
client.addHandler(new DefaultReconnectHandler(client));
client.addHandler(new DefaultReconnectHandler(client, 3));
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

You can connect by assigning address in the class and not at the time of connection.
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
### When you connect
Client connection method in version 1.0.0
```java
// available only in 1.0.0
client.on("connect", args -> {
  SocketOpenReason reason = (SocketOpenReason) args.value("reason");
  if(reason == null); {
    client.log(Level.INFO, "First connection to the established server.");
    return;
  }

  if(reason == SocketOpenReason.RECONNECT) {
    client.log(Level.INFO, "Reconnected to the server.");
  }
});
```
**NOTE: As of version 1.0.1 the event when connecting was replaced by use directly in connection method.**

### When to disconnect
This event has no arguments.
```java
client.on("disconnect", args -> {
  // ...
});

// available only in 1.0.0
// or
client.getWorker().on("end", args -> {

});
```

### When receiving message from server
```java
client.on("message", args -> {
  Message message = (Message) args.value("message");
  client.log(Level.INFO, "Message received from the server: " + message.toJson());
});
```

### Connection errors
Disconnection handlers are called here, for example:\
The `DefaultDisconnectHandler` is called when the` SocketCloseReason` of this event is `RESET`.
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
  - My [Discord](https://discordapp.com) NT#2374
  - Our site [MotoNetwork](https://motocrack.net)
