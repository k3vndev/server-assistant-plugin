package me.kev.sva.chat.message;

public abstract class ChatMessage {
  public final String content;

  public ChatMessage(String content) {
    this.content = content;
  }
}
