package me.kev.sva.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;

import me.kev.sva.ServerAssistantPlugin;
import me.kev.sva.utils.MessageSender;
import net.kyori.adventure.text.Component;

public class CommandManager implements TabExecutor {
  private final ServerAssistantPlugin plugin;

  public CommandManager(ServerAssistantPlugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(
      CommandSender sender,
      Command command,
      String label,
      String[] args) {

    if (!command.getName().equalsIgnoreCase("sva")) {
      return false;
    }

    if (!sender.hasPermission("sva.admin")) {
      MessageSender.Error("You don't have permission to do that.");
      return true;
    }

    // /sva reload
    if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
      try {
        // Reload config and reinitialize plugin state
        if (plugin instanceof ServerAssistantPlugin) {
          ((ServerAssistantPlugin) plugin).reloadPlugin();
        } else {
          plugin.reloadConfig();
        }

        MessageSender.Success("Plugin reloaded!");

      } catch (Exception e) {
        plugin.getLogger().severe(
            "Config reload failed: "
                + e.getClass().getName()
                + ": "
                + e.getMessage());

        e.printStackTrace();

        MessageSender.Error("Config reload failed. Check console.");
      }

      return true;
    }

    // /sva playerchatmode <mode>
    if (args.length == 2 && args[0].equalsIgnoreCase("playerchatmode")) {
      String mode = args[1].toLowerCase(Locale.ROOT);

      if (!List.of("always", "mention", "disabled").contains(mode)) {
        MessageSender.Error(
            "Invalid mode. Use: always, mention, or disabled.");

        return true;
      }

      plugin.getConfig().set(
          "request-triggers.player-messages.mode",
          mode);

      plugin.saveConfig();

      MessageSender.Success(
          "Player message mode set to: " + mode);

      return true;
    }

    sender.sendMessage(Component.text(
        "Usage: /sva reload | /sva playerchatmode <always|mention|disabled>"));

    return true;
  }

  @Override
  public List<String> onTabComplete(
      CommandSender sender,
      Command command,
      String alias,
      String[] args) {

    if (!command.getName().equalsIgnoreCase("sva")) {
      return Collections.emptyList();
    }

    if (!sender.hasPermission("sva.admin")) {
      return Collections.emptyList();
    }

    if (args.length == 1) {
      List<String> completions = new ArrayList<>(
          List.of("reload", "playerchatmode"));

      List<String> matches = new ArrayList<>();

      StringUtil.copyPartialMatches(
          args[0],
          completions,
          matches);

      Collections.sort(matches);

      return matches;
    }

    if (args.length == 2
        && args[0].equalsIgnoreCase("playerchatmode")) {

      List<String> matches = new ArrayList<>();

      StringUtil.copyPartialMatches(
          args[1],
          List.of("always", "mention", "disabled"),
          matches);

      return matches;
    }

    return Collections.emptyList();
  }
}