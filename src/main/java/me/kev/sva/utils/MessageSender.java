package me.kev.sva.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;

public abstract class MessageSender {
  public static final String HEADER = "[ServerAssistant]";

  public static void Success(String message) {
    SendMessage(ChatColor.BLUE, message);
  }

  public static void Success(CommandSender sender, String message) {
    SendMessage(sender, ChatColor.BLUE, message);
  }

  public static void Error(String message) {
    SendMessage(ChatColor.RED, message);
  }

  public static void Error(CommandSender sender, String message) {
    SendMessage(sender, ChatColor.RED, message);
  }

  public static void Dev(String message) {
    SendMessage(ChatColor.GREEN, message);
  }

  public static void Dev(CommandSender sender, String message) {
    SendMessage(sender, ChatColor.GREEN, message);
  }

  static void SendMessage(ChatColor color, String message) {
    SendMessage(Bukkit.getConsoleSender(), color, message);
  }

  static void SendMessage(CommandSender sender, ChatColor color, String message) {
    String formattedMessage = ChatColor.translateAlternateColorCodes('&', message);

    if (sender != null) {
      sender.sendMessage(color + HEADER + " " + formattedMessage);
      return;
    }

    Bukkit.getConsoleSender().sendMessage(color + HEADER + " " + formattedMessage);
  }
}
