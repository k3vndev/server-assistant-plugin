package me.kev.sva.custom_commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import me.kev.sva.ServerAssistantPlugin;
import me.kev.sva.custom_commands.commands.CustomCommandListen;
import me.kev.sva.custom_commands.commands.CustomCommandReload;
import me.kev.sva.utils.MessageSender;

public class CustomCommandManager implements TabExecutor {
  private final ServerAssistantPlugin plugin;

  public CustomCommandManager(ServerAssistantPlugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(
      CommandSender sender,
      org.bukkit.command.Command command,
      String label,
      String[] args) {

    if (!command.getName().equalsIgnoreCase("sva")) {
      return false;
    }

    if (!sender.hasPermission("sva.admin")) {
      MessageSender.Error("You don't have permission to do that.");
      return true;
    }

    // Create command instances and try to match them
    List<CustomCommand> activeCommands = List.of(
        new CustomCommandReload(sender, plugin),
        new CustomCommandListen(sender, plugin));

    for (CustomCommand cmd : activeCommands) {
      if (cmd.matches(args)) {
        return cmd.execute(args);
      }
    }

    // No command matched, show usage
    sender.sendMessage(net.kyori.adventure.text.Component.text(
        "Usage: /sva reload | /sva listen <playerchat> [always|mention|smart|disabled]"));

    return true;
  }

  @Override
  public List<String> onTabComplete(
      CommandSender sender,
      org.bukkit.command.Command command,
      String alias,
      String[] args) {

    if (!command.getName().equalsIgnoreCase("sva")) {
      return Collections.emptyList();
    }

    if (!sender.hasPermission("sva.admin")) {
      return Collections.emptyList();
    }

    // If first argument is incomplete, suggest command names
    if (args.length == 1) {
      List<String> completions = List.of("reload", "listen");
      List<String> matches = new ArrayList<>();
      org.bukkit.util.StringUtil.copyPartialMatches(args[0], completions, matches);
      Collections.sort(matches);
      return matches;
    }

    // Otherwise, let each command provide its own completions
    List<CustomCommand> activeCommands = List.of(
        new CustomCommandReload(sender, plugin),
        new CustomCommandListen(sender, plugin));

    for (CustomCommand cmd : activeCommands) {
      List<String> completions = cmd.getTabCompletions(args);
      if (!completions.isEmpty()) {
        return completions;
      }
    }

    return Collections.emptyList();
  }
}