package me.kev.sva.chat.message;

import org.bukkit.plugin.java.JavaPlugin;

import me.kev.sva.chat.assistant.AssistantResponse;

public class AssistantChatMessage extends ChatMessage {
  public final AssistantResponse response;

  public AssistantChatMessage(JavaPlugin plugin, String content) {
    super(plugin, content);
    this.response = new AssistantResponse(plugin, content);
  }
}
