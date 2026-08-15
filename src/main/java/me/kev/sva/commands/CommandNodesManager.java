package me.kev.sva.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import me.kev.sva.ServerAssistantPlugin;
import me.kev.sva.commands.sva.CNSva;

public class CommandNodesManager implements TabExecutor {
  private final ServerAssistantPlugin plugin;

  public CommandNodesManager(ServerAssistantPlugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(
      CommandSender sender,
      org.bukkit.command.Command command,
      String label,
      String[] args) {

    CommandNode sva = new CNSva(sender, plugin);

    List<String> argsList = new ArrayList<>(List.of(args));
    argsList.addFirst(command.getName());

    if (sva.matches(argsList)) {
      argsList.removeFirst();
      sva.execute(argsList);
      return true;
    }
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

    List<String> remainingArgs = new ArrayList<>(List.of(args));
    return new CNSva(sender, plugin).getTabCompletions(remainingArgs);
  }
}