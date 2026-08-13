package me.kev.sva.chat.message;

public class BroadcastChatMessage extends ChatMessage {
  public final String header = "[Global] ";

  public BroadcastChatMessage(String content) {
    super(content);
  }
}
