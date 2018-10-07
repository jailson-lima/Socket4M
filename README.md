# Socket4M
This is a project maintained by me [@DevNatan](https://github.com/DevNatan) and [@wiljafor1](https://github.com/wiljafor1).\
<br>
We use this in our projects and we will be updating it constantly, if you like the project and want to collaborate, contact us or make a **Pull Request** directly.\
If you are in doubt about something or want us to change any line of code, make an **Issue** that we will deal with.
<br>
  
## Getting started
### First:
  - The current version is 2.1.
  - Read about [Socket](https://docs.oracle.com/javase/8/docs/api/java/net/Socket.html).
  - Read about [SocketChannel](https://docs.oracle.com/javase/8/docs/api/java/nio/channels/SocketChannel.html).
  - **Java versions smaller than 8 are not supported.**
  
## Connection
The `Connection` class is responsible for handling connection methods and loading default client properties such as: ip address, port, and connection handlers, message, and errors.
<br><br>
It is also responsible for loading our `SocketChannel` and making changes to it.
To instantiate a connection object you need the IP address and the client port.
```java
Connection c = new Connection("localhost", 4434);
```
## Handlers
Connection, message, and error handlers can be added to your connection object to make it easier to receive messages, error handling, and connections.\
Currently there are 3 handlers: of errors, messages and connections.
<br>
### Error handler
Use `ErrorHandler` to handle errors easily.
```java
class MyErrorHandler extends ErrorHandler {

    public void on(Throwable throwable, Error error) {
        // Error is an enumeration.
        // Explore this and find out what types of errors you can handle.
    }

}

c.setErrorHandler(new MyErrorHandler());
```
<br>

### Message handler
Use `MessageHandler` to handle messages that you have sent or messages you have received from the server.
```java
class MyMessageHandler extends MessageHandler {

    // Your messages are sent to the server.
    public void onWrite(Message m) {

    }

    // Messages received from the server.
    public void onRead(Message m) {

    }

}

c.setMessageHandler(new MyMessageHandler());
```
<br>

### Connection handler
The connection handler is responsible for handling events when you connect, reconnect, disconnect, attempt to connect, or fail to connect.
```java
class MyConnectionHandler extends ConnectionHandler {

    public void onConnect(Connection c) {
        System.out.println("Connected successfully!");
    }

    public void onDisconnect(Connection c) {
        System.out.println("Disconnected successfully!");
    }

    public void onFailConnect(Connection c) {
        System.out.println("Failed to connect to the server.");
    }

    public void onReconnect(Connection c) {

    }

    public void onTryConnect(Connection c) {
    
    }

}

c.setConnectionHandler(new MyConnectionHandler());
```
<br>

## Worker
The `Worker` is the heart of our client, it is he who reads the` Reader` messages or writes messages to the `Writer` of the client.\
It is running in a constant loop, and will only be interrupted if the client connection is interrupted.\
For a client to function it is essential that your worker is defined.
```java
Worker w = new Worker();

// synchronize error handler.
w.setErrorHandler(c.getErrorHandler());
```
<br>

### Reader
This is the class that reads internally the messages received from the server.
```java
// any implementation of BlockingQueue.
// the reader buffer.
w.setReader(new Reader(new LinkedBlockingQueue<>(), 1024));
```
<br>

### Writer
This is the class that handles the messages the client writes and sends them to the server.
```java
// any implementation of BlockingQueue.
// the writer buffer.
w.setWriter(new Writer(new LinkedBlockingQueue<>(), 1024));
```
<br>

## Client
After defining your worker, handlers, reader, writer, connection and other properties, only the client is missing.
```java
Client client = new Client();
client.setConnection(c);
client.setWorker(w);
```

### Client methods
Their name is already self-explanatory.
```java
client.connect();
client.disconnect()
client.reconnect();
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
client.send(message);
```
