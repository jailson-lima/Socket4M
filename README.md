# Socket4M
This is a project maintained by me [@DevNatan](https://github.com/DevNatan), [@wiljafor1](https://github.com/wiljafor1) and [@JPereirax](https://github.com/JPereirax).\
<br>
We use this in our projects and we will be updating it constantly, if you like the project and want to collaborate, contact us or make a **Pull Request** directly.\
If you are in doubt about something or want us to change any line of code, make an **Issue** that we will deal with.
<br>

**Table of Contents:**
1. [Getting Started](#getting-started)
    - [First](#first)
    - [Read about](#read-about)
    - [Connection](#connection)
2. [Client](#client)
    - [Handlers](#handlers)
        - [Error Handler](#error-handler)
        - [Message Handler](#message-handler)
        - [Connection Handler](#connection-handler)
    
<br>
  
## Getting started
### First:
  - The current version is 2.1.
  - Java versions smaller than 8 are not supported.
  - [Pull Requests](https://github.com/DevNatan/Socket4M/pulls) are totally acceptable, contributions are always welcome.
  - Have some questions, do an [Issue](https://github.com/DevNatan/Socket4M/issues).
  
### Read about:
  * [Future](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Future.html)
  * [Socket](https://docs.oracle.com/javase/8/docs/api/java/net/Socket.html)
  * [SocketChannel](https://docs.oracle.com/javase/8/docs/api/java/nio/channels/SocketChannel.html)
  * [AsynchronousChannel](https://docs.oracle.com/javase/8/docs/api/java/nio/channels/AsynchronousChannel.html)
  * [AsynchronousSocketChannel](https://docs.oracle.com/javase/8/docs/api/java/nio/channels/AsynchronousSocketChannel.html)
  * [AsynchronousServerSocketChannel](https://docs.oracle.com/javase/8/docs/api/java/nio/channels/AsynchronousServerSocketChannel.html)
  
### Connection
The `Connection` class is responsible for handling connection methods and loading default client properties such as: ip address, port, and connection handlers, message, and errors.
<br><br>
It is also responsible for loading our `SocketChannel` and making changes to it.
To instantiate a connection object you need the IP address and the client port.
```java
Connection c = new Connection(new InetSocketAddress(4434));
// or
Connection c = new Connection(new InetSocketAddres("address", 4434));
```

# Client
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
w.setReader(new Reader(c, new LinkedBlockingQueue<>(), 1024));
```
<br>

### Writer
This is the class that handles the messages the client writes and sends them to the server.
```java
// any implementation of BlockingQueue.
// the writer buffer.
w.setWriter(new Writer(c, new LinkedBlockingQueue<>(), 1024));
```
<br>

## Message
Declaring
```java
new Message<>(map); // Map<String, Object>
```

Use a message builder to easily create them.
```java
Message m = Message.builder()
  .with("question", "Github is good?")
  .with("answer", true)
  .build();
```

The message
```java
/* 
    to return the json content of the message
    for example: {"question":"Github is good?", "answer":true}
 */
String json = message.toJson();

// sending the message
client.send(message);
```

A plain text:
```java
client.send("My plain text");

/*
    the server will get
    {"text", "My plain text"}  
*/
```
<br>

## Finishing
After defining your worker, handlers, reader, writer, connection and other properties, only the client is missing.
```java
Client client = new Client();
client.setConnection(c);
client.setWorker(w);
```

### Methods
Their name is already self-explanatory.
```java
client.connect();
client.disconnect()
client.reconnect();
```
