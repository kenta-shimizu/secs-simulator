# secs-simulator

![image](https://github.com/kenta-shimizu/secs-simulator/tree/master/docs/swing.png)

This is SECS-Simulator Java application.

[github.io](https://kenta-shimizu.github.io/secs-simulator/index.html)

## Extend SML

### Now

- `<NOW[16]>`

  ```
  LocalDateTime.now().format(DateTimeFormatter.ofPattern("uuuuMMddHHmmssSS");
  ```

- `<NOW[12]>`

  ```
  LocalDateTime.now().format(DateTimeFormatter.ofPattern("uuMMddHHmmss");
  ```

### Auto number

- `<U4AUTO>`
- `<U8AUTO>`
- `<I4AUTO>`
- `<I8AUTO>`
