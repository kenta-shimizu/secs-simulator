# secs-simulator

![image](https://kenta-shimizu.github.io/secs-simulator/swing.png)

This is SEMI-SECS-Simulator Java application.

## Get Application, How to use

[github.io](https://kenta-shimizu.github.io/secs-simulator/index.html)

## Related repositories
 
 - [secs4java8](https://github.com/kenta-shimizu/secs4java8)
 - [json4java8](https://github.com/kenta-shimizu/json4java8)

## Extend SML

SML is from [PEER Group](https://www.peergroup.com/expertise/resources/secs-message-language/)

### Now

- &lt;NOW[16]&gt;

  ```java
  DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuuMMddHHmmssSS");
  LocalDateTime.now().format(dtf);
  ```

- &lt;NOW[12]&gt;

  ```java
  DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuMMddHHmmss");
  LocalDateTime.now().format(dtf);
  ```

  See also [s2f18.sml](/src/sml-template/host/s2f18.sml), [s2f31.sml](/src/sml-template/host/s2f31.sml)

### Auto number

Using `AtomicLong#incrementAndGet`

- &lt;U4AUTO&gt;
- &lt;U8AUTO&gt;
- &lt;I4AUTO&gt;
- &lt;I8AUTO&gt;

  See also [s2f33-delete-all.sml](/src/sml-template/host/s2f33-delete-all.sml), [s2f35-example.sml](/src/sml-template/host/s2f35-example.sml)


## Auto-replies

### Auto-reply

Auto-reply conditions.

- `AbstractSecsSimulatorConfig#autoReply#booleanValue == true`
- Received primary message has wbit.
- Received primary message Function-number is odd number.
- Has only ONE added SML to reply.

### Auto-reply-S9Fy

Auto-Reply-S9Fy conditions.

- `AbstractSecsSimulatorConfig#autoReplyS9Fy#booleanValue == true`

#### Auto-Reply-S9F1

Reply if received message device-id is not valid.

#### Auto-Reply-S9F3

Reply if has no added SML to reply Stream-number.

#### Auto-Reply-S9F5

Reply if has no added SML to reply Function-number.

#### Auto-Reply-S9F9

Reply if T3-Timeout.

### Auto-reply-SxF0

Auto-Reply-SxF0 conditions.

- `AbstractSecsSimulatorConfig#autoReplySxF0#booleanValue == true`
- Received primary message has wbit.
- Has no added SML to reply (e.g. if received "S1F1 W", not added "S1F2")
