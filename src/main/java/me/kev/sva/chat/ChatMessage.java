package me.kev.sva.chat;

public class ChatMessage {
  final String senderName;
  final boolean isAssistant;
  final String content;

  public ChatMessage(String senderName, boolean isAssistant, String content) {
    this.senderName = senderName;
    this.isAssistant = isAssistant;
    this.content = content;
  }
}
