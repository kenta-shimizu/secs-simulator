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

  See also [s2f17.sml](/src/sml-template/equip/s2f17.sml), [s2f31.sml](/src/sml-template/host/s2f31.sml)

### Auto number

Using `AtomicLong#incrementAndGet`

- &lt;U4AUTO&gt;
- &lt;U8AUTO&gt;
- &lt;I4AUTO&gt;
- &lt;I8AUTO&gt;

  See also [s2f33-delete-all.sml](/src/sml-template/host/s2f33-delete-all.sml), [s2f35-example.sml](/src/sml-template/host/s2f35-example.sml)
