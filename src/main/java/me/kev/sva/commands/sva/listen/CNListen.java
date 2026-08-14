package me.kev.sva.commands.sva.listen;

import java.util.List;

import org.bukkit.command.CommandSender;

import me.kev.sva.ServerAssistantPlugin;
import me.kev.sva.commands.CommandNode;

/**
 * Handles the /sva listen command with support for multiple subcommands.
 * Structure: /sva listen <subcommand>
 */
public class CNListen extends CommandNode {
  public CNListen(CommandSender sender, ServerAssistantPlugin plugin) {
    super(sender, plugin);
  }

  @Override
  public String getName() {
    return "listen";
  }

  @Override
  public List<CommandNode> getSubCommands() {
    return List.of(
        new CNPlayerChat(sender, plugin));
  }

  @Override
  public boolean execute(List<String> args) {
    if (args.isEmpty()) {
      throwMissingArgumentsError();
      return false;
    }

    return executeSubcommands(args);
  }
}
