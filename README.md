# Socket4M
Cliente socket para interação com um servidor de protocolo TCP.\n

### Atenção
Não utilize nada deste repositório sem ter pleno conhecimento do que está fazendo.\n

### Informações
  - Saiba mais sobre a emissão de eventos em [Events4J](https://github.com/theShadow89/Events4J).
  
### Assincronismo
Ainda não implementado
  
# Exemplos
### Utilities
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

// socket.setOOBInline
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
  
## Precisando de ajuda?
  - Meu [Discord](https://discordapp.com) NT#2374
  - Site [MotoNetwork](https://motocrack.net)
