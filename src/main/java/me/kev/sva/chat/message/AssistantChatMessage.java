package me.kev.sva.chat.message;

import me.kev.sva.ServerAssistantPlugin;
import me.kev.sva.chat.assistant.AssistantResponse;

public class AssistantChatMessage extends ChatMessage {
  public final AssistantResponse response;

  public AssistantChatMessage(ServerAssistantPlugin plugin, String content) {
    super(plugin, content);
    this.response = new AssistantResponse(plugin, content);
  }
}
