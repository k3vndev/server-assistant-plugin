package me.kev.sva.utils;

import org.bukkit.Bukkit;

import net.md_5.bungee.api.ChatColor;

public abstract class MessageSender {
  public static final String HEADER = "[ServerAssistant]";

  public static void Success(String message) {
    SendMessage(ChatColor.BLUE, message);
  }

  public static void Error(String message) {
    SendMessage(ChatColor.RED, message);
  }

  static void SendMessage(ChatColor color, String message) {
    Bukkit.getConsoleSender().sendMessage(
        color + HEADER + " " + message);
  }
}
