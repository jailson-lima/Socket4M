# Socket4M
Cliente socket para interação com um servidor de protocolo TCP.

### Atenção
Não utilize nada deste repositório sem ter pleno conhecimento do que está fazendo.

### Informações
  - Saiba mais sobre a emissão de eventos em [Events4J](https://github.com/theShadow89/Events4J).
  - Leia sobre [cliente Socket](https://docs.oracle.com/javase/8/docs/api/java/net/Socket.html).
  - Leia sobre [servidor Socket](https://docs.oracle.com/javase/8/docs/api/java/net/ServerSocket.html)
  - **Versões do Java menores que 8 não são suportadas.**
  
### Assincronismo e processamento paralelo
Ainda não implementado
  
# Exemplos
### Utilidades
A classe [Utilities](https://github.com/MotoCrack/Socket4M/blob/master/src/main/java/me/devnatan/socket4m/client/Utilities.java) são utilidades do cliente que podem ser usadas para auxiliamento, incluindo recebimento de mensagens.

```java
Utilities utilities = new Utilities();

// Ative a opção "debug" para ver detalhes no console.
utilities.setDebug(true);
```

Adicione um manipulador de mensagens. 
O número `100` no `ArrayBlockingQueue` é a capacidade da fila.\
Outros tipos de `Queue` podem ser usados, saiba mais em [implementações de BlockingQueue](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/BlockingQueue.html).
```java
utilities.setMessageHandler(new MessageHandler(new ArrayBlockingQueue<>(100)));
```

### Cliente
```java
Client client = new Client();

// Defina as utilidades do cliente.
client.setUtilities(utilities);
```

#### Opções do cliente
Este cliente suporte algumas opções que a classe [Socket](https://docs.oracle.com/javase/8/docs/api/java/net/Socket.html) tem por padrão.
```java
// socket.setKeepAlive(true);
client.addOption("KEEP_ALIVE", true);

// socket.setOOBInline(true);
client.addOption("OUT_OF_BAND_DATA", true);
```

### Manipuladores
Ainda não há muitos manipuladores disponíveis, somente um.\
O `DefaultReconnectHandler` pode ser usado para conexões que necessitam de suporte para reconectamento automático.\
Ele é chamado quando o servidor deixa de responder ao cliente ou fecha a conexão inesperadamente.\

**OBS: `DefaultReconnectHandler` não se aplica a conexões terminadas com motivo `TIMEOUT`**
```java
// 3 é o número de tentativas de reconectamento
client.addHandler(new DefaultReconnectHandler(client, client.getWorker(), 3));

// também é possível tentar reconectar somente uma vez não atribuindo nenhum valor
client.addHandler(new DefaultReconnectHandler(client, client.getWorker()));

// ou atribuindo um valor menor ou igual a zero.
client.addHandler(new DefaultReconnectHandler(client, client.getWorker(), 0));
```

### Estabelecendo conexão
Atribua um endereço de IP ao cliente **COM PORTA**.\
É possível atribuir endereço e porta diretamente na classe.
```java
client.setAddress("127.0.0.1");
client.setPort(8080);
```

Ou atribuir somente no momento da conexão.
```java
client.connect("127.0.0.1", 8080);
```

Atribuindo `TIMEOUT` a conexão.
```java
// 10 segundos
client.setTimeout(10000);

// ou
client.connect("127.0.0.1", 8080, 10000);
```

É possível conectar atribuindo endereço na classe e não no momento da conexão.
```java
// Endereço e porta já estão atribuidos na classe.
client.connect();
```
**OBS: Antes de estabelecer conexão certifique-se que definiu os eventos anteriormente.**

## Eventos
Quando o cliente estabelece conexão com o servidor

### Sintaxe
```
client.on("evento", args -> {
  // ...
});
```
### Quando conectar
```java
client.on("connect", args -> {
  SocketOpenReason reason = (SocketOpenReason) args.value("reason");
  if(reason == null); {
    client.log(Level.INFO, "Primeira conexão com o servidor estabelecida.");
    return;
  }

  if(reason == SocketOpenReason.RECONNECT) {
    client.log(Level.INFO, "Reconectado com o servidor.");
  }
});
```

### Quando desconectar
Este evento não contém argumentos.
```java
client.on("disconnect", args -> {
  // ...
});

// ou
client.getWorker().on("end", args -> {

});
```

### Ao receber mensagem do servidor
```java
client.on("message", args -> {
  Message message = (Message) args.value("message");
  client.log(Level.INFO, "Mensagem do servidor:");
  client.log(Level.INFO, "  - Texto: " + message.getText());
  client.log(Level.INFO, "  - Mapa: " + message.getValues());
  client.log(Level.INFO, "  - JSON: " + message.json());
});
```

### Erros na conexão
Manipuladores de desconexão são chamados aqui, por exemplo:\
O `DefaultDisconnectHandler` é chamado quando o `SocketCloseReason` deste evento é `RESET`.
```java
client.on("error", args -> {
  Throwable throwable = (Throwable) arguments.get("throwable").getValue();
  SocketCloseReason reason = (SocketCloseReason) arguments.get("reason").getValue();
  if(reason == SocketCloseReason.RESET) {
    // Chamado independentemente de haver um manipulador de reconexão.
    client.log(Level.SEVERE, "Conexão com o servidor fechada, tentando reconectar...");
    return;
  }

  if(reason == SocketCloseReason.REFUSED) {
    client.log(Level.SEVERE, "Não foi possível conectar-se ao servidor.");
    return;
  }

  ((Throwable) arguments.get("throwable").getValue()).printStackTrace();
});
```
  
## Precisando de ajuda?
  - Meu [Discord](https://discordapp.com) NT#2374
  - Site [MotoNetwork](https://motocrack.net)
