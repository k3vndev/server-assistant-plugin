package me.kev.sva.chat.message;

import me.kev.sva.ServerAssistantPlugin;

public class BroadcastChatMessage extends ChatMessage {
  public final String header = "[Global] ";

  public BroadcastChatMessage(ServerAssistantPlugin plugin, String content) {
    super(plugin, content);
  }
}
