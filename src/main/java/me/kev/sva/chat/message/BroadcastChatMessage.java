package me.kev.sva.chat.message;

import org.bukkit.plugin.java.JavaPlugin;

public class BroadcastChatMessage extends ChatMessage {
  public final String header = "[Global] ";

  public BroadcastChatMessage(JavaPlugin plugin, String content) {
    super(plugin, content);
  }
}
