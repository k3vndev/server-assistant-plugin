package me.kev.sva.chat.message;

import org.bukkit.plugin.java.JavaPlugin;

public abstract class ChatMessage {
  public final String content;
  protected final JavaPlugin plugin;

  public ChatMessage(JavaPlugin plugin, String content) {
    this.plugin = plugin;
    this.content = content;
  }
}
