# ServerAssistant (SVA)

An AI-powered assistant for Minecraft servers, designed to be **modular, configurable, and server-agnostic**.

SVA allows you to add an AI assistant to your server that can understand player conversations, access server-specific information, use tools, and react to server events.

## Features

* 🤖 **AI-powered conversations** — Chat naturally with players.
* 🧠 **Configurable personality** — Define the assistant's personality, tone, and behavior.
* 📚 **Server wiki** — Give the assistant access to custom server information through `config.yml`.
* 🛠️ **Tool system** — Let the assistant perform actions and retrieve information.
* 💬 **Smart chat triggers** — Respond to every message, mentions, or active conversations.
* 📡 **Server events** — React to events such as joins, deaths, advancements, and quits.
* ⚙️ **Highly configurable** — Most behavior can be changed without modifying the plugin.
* 🧩 **Modular architecture** — Tools, commands, conversations, and context are separated so new features can be added easily.

## How It Works

SVA maintains a conversation with the assistant and provides it with contextual information about the server.

When a player sends a message, SVA determines whether it should trigger an AI request based on the configured trigger mode.

The assistant can then:

1. Respond with one or more chat messages.
2. Use one or more available tools.
3. Do both.
4. Decide not to respond.

Responses use a structured YAML format internally:

```yaml
messages:
  - "Let me check that."
tool-calls:
  - "wiki economy"
```

This allows multiple messages and tool calls to be handled reliably.

## Server Wiki

Server-specific information can be configured directly in `config.yml`.

```yaml
tools:
  wiki:
    activation: smart
    pages:
      commands:
        description: Commands available to players.
        content: |
          /spawn
          /home
          /tpa <player>

      economy:
        description: Server economy and currency information.
        content: |
          16 emeralds = 1 currency item
```

The assistant receives an index of available wiki sections and can request detailed information when necessary.

This keeps server-specific knowledge separate from the assistant's core instructions.

## Tools

SVA has a modular tool system that allows the assistant to interact with the server.

Planned and implemented tools include:

* **Wiki** — Retrieve configured server information.
* **Player Data** — Retrieve information about an online player.
* **Inventory** — Inspect a player's inventory when additional detail is needed.
* **Mute** — Temporarily mute a player.
* **Sound** — Play sounds from a curated list.
* **Lightning** — Create a visual/audio lightning effect without damaging players.
* **Schedule** — Schedule a future assistant action or follow-up.

Tools can be individually enabled or disabled.

The system is designed so additional tools can be added without changing the assistant's core conversation logic.

## Chat Triggers

SVA supports several ways to determine when player messages should trigger an AI request:

```yaml
request-triggers:
  player-messages:
    mode: mention
```

Available modes:

* `always` — Every player message can trigger the assistant.
* `mention` — Only messages mentioning the assistant trigger a request.
* `smart` — A mention starts a conversation; subsequent messages continue triggering requests until the conversation becomes inactive.
* `disabled` — Player messages never trigger requests.

The assistant can still choose not to respond even when a request is triggered.

## Server Events

SVA can provide server events as conversational context.

Examples include:

* Player deaths
* Player joins
* Player quits
* Advancements
* Other events can be added through the modular event system.

Events can be enabled or disabled individually.

## Personality

The assistant's personality can be configured independently from SVA's core instructions.

This allows server owners to customize:

* Personality
* Tone
* Humor
* Conversational style
* Behavior
* Character traits

Core system instructions and response formatting remain protected so personality configuration cannot break the assistant's tool or response protocol.

## Commands

SVA provides administrative commands for configuring and managing the assistant.

```text
/sva reload
/sva listen ...
```

The command system uses a tree-based architecture, making it easy to add commands and subcommands while keeping each command isolated.

## Configuration

SVA is designed to keep configuration simple while allowing extensive customization.

Important configuration areas include:

```text
config.yml
├── assistant-name
├── prompt
├── chat
├── request-triggers
└── advanced-context
    └── wiki
```

Server-specific data, assistant behavior, trigger settings, and contextual information can all be configured independently.

## Architecture

SVA is built around several independent systems:

```text
Chat
 │
 ▼
Conversation Manager
 │
 ▼
Assistant Manager
 │
 ├── Context
 ├── Personality
 ├── Tools
 └── Response
       │
       ├── Messages
       └── Tool Calls
```

This separation makes it possible to expand the plugin without turning the assistant into one giant pile of spaghetti. 🙏

## Requirements

* Paper
* Java 21+
* An AI provider/API supported by the plugin

## Status

SVA is currently under development.

The core assistant, structured responses, configurable personality, server wiki, tool architecture, conversation handling, smart chat triggers, and event context systems are already in place.

More tools and server integrations are being added incrementally.
