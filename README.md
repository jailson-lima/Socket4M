# Socket4M
Cliente socket para interação com um servidor de protocolo TCP.

### Informações
  - **NÃO UTILIZE** códigos deste repositório em modo de produção.
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
O número `100` no `ArrayBlockingQueue` é a capacidade da fila.
Outros tipos de `Queue` podem ser usados, saiba mais em [implementações de BlockingQueue](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/BlockingQueue.html).
```java
utilities.setMessageHandler(new MessageHandler(new ArrayBlockingQueue<>(100)));
```

### Cliente
Criando cliente:
```java
Client client = new Client();

// Definida as utilidades do cliente.
client.setUtilities(utilities);
```
  
## Precisando de ajuda?
  - Meu [Discord](https://discordapp.com) NT#2374
  - Site [MotoNetwork](https://motocrack.net)
