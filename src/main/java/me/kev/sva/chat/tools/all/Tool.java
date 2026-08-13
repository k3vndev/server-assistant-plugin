package me.kev.sva.chat.tools.all;

import me.kev.sva.ServerAssistantPlugin;

public abstract class Tool {
  protected final ServerAssistantPlugin plugin;
  public final String name;

  public Tool(ServerAssistantPlugin plugin, String name) {
    this.name = name;
    this.plugin = plugin;
  }
}
