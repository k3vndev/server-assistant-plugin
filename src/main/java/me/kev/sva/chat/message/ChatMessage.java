package me.kev.sva.chat.message;

import me.kev.sva.ServerAssistantPlugin;

public abstract class ChatMessage {
  public final String content;
  protected final ServerAssistantPlugin plugin;

  public ChatMessage(ServerAssistantPlugin plugin, String content) {
    this.plugin = plugin;
    this.content = content;
  }
}
