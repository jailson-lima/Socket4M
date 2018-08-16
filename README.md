# Socket4M
Cliente socket para interação com um servidor de protocolo TCP.

### Informações
  - **NÃO UTILIZE** códigos deste repositório em modo de produção.
  - Saiba mais sobre a emissão de eventos em [Events4J](https://github.com/theShadow89/Events4J).
  
### Assincronismo
Ainda não implementado
  
# Exemplos
### Core
Atribuindo instância:
```java
Core core = new Core();
```

Ative a opção `debug` se quiser ver detalhes no console.
```java
core.setDebug(true);
```

Adicione um manipulador de mensagens.
O número `100` no `ArrayBlockingQueue` é a capacidade da fila.
Outros tipos de `Queue` podem ser usados, saiba mais em [implementações de BlockingQueue](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/BlockingQueue.html).
```java
core.setMessageHandler(new MessageHandler(new ArrayBlockingQueue<>(100)));
```

E por fim defina a instância.
```java
Core.setInstance(core);
```

### Cliente
Criando cliente:
```java
Client client = new Client();
```
  
## Precisando de ajuda?
  - Meu [Discord](https://discordapp.com) NT#2374
  - Site [MotoNetwork](https://motocrack.net)
