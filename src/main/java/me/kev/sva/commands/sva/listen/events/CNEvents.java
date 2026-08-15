package me.kev.sva.commands.sva.listen.events;

import java.util.List;

import org.bukkit.command.CommandSender;

import me.kev.sva.ServerAssistantPlugin;
import me.kev.sva.commands.CommandNode;

public class CNEvents extends CommandNode {

  public CNEvents(CommandSender sender, ServerAssistantPlugin plugin) {
    super(sender, plugin);
  }

  @Override
  public String getName() {
    return "events";
  }

  @Override
  public List<CommandNode> getSubCommands() {
    return List.of(
        new EventsCommandNode(sender, plugin, "player-death", "death"),
        new EventsCommandNode(sender, plugin, "player-advancement", "advancement"),
        new EventsCommandNode(sender, plugin, "player-join-quit", "joinquit"));
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
