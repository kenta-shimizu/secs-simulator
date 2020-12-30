# secs-simulator

![image](https://kenta-shimizu.github.io/secs-simulator/swing.png)

This is SEMI-SECS-Simulator Java application.

## Get Application, How to use

[github.io](https://kenta-shimizu.github.io/secs-simulator/index.html)

## Related repositories
 
 - [secs4java8](https://github.com/kenta-shimizu/secs4java8)
 - [json4java8](https://github.com/kenta-shimizu/json4java8)

## Extend SML

### Now

- &lt;NOW[16]&gt;

  ```
  LocalDateTime.now().format(DateTimeFormatter.ofPattern("uuuuMMddHHmmssSS"));
  ```

- &lt;NOW[12]&gt;

  ```
  LocalDateTime.now().format(DateTimeFormatter.ofPattern("uuMMddHHmmss"));
  ```

### Auto number

using `AtomicLong#incrementAndGet`

- &lt;U4AUTO&gt;
- &lt;U8AUTO&gt;
- &lt;I4AUTO&gt;
- &lt;I8AUTO&gt;
