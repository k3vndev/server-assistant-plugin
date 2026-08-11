## Normal reply

```yml
tool-calls: []

messages:
  - "Hello world, I'm a really smart assistant."
```

## Tool calls
```yml
tool-calls:
  - "/fetch raids"

messages:
  - "Let me check that for you."
```

# No response
```yml
tool-calls: []
messages: []
```