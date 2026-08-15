package me.kev.sva.commands.sva.listen;

import java.util.List;

import org.bukkit.command.CommandSender;

import me.kev.sva.ServerAssistantPlugin;
import me.kev.sva.commands.CommandNode;
import me.kev.sva.commands.sva.listen.events.CNEvents;

/**
 * Handles the /sva listen command with support for multiple subcommands.
 * Structure: /sva listen <subcommand>
 */
public class CNListener extends CommandNode {
  public CNListener(CommandSender sender, ServerAssistantPlugin plugin) {
    super(sender, plugin);
  }

  @Override
  public String getName() {
    return "listener";
  }

  @Override
  public List<CommandNode> getSubCommands() {
    return List.of(
        new CNPlayerChat(sender, plugin),
        new CNEvents(sender, plugin));
  }

  @Override
  public boolean execute(List<String> args) {
    if (args.isEmpty()) {
      throwMissingArgumentsError();
      return false;
    }

    if (executeSubcommands(args)) {
      return true;
    }

    throwInvalidArgumentError();
    return false;
  }
}
