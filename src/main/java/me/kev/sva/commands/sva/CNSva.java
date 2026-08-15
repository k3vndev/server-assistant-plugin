package me.kev.sva.commands.sva;

import java.util.List;

import org.bukkit.command.CommandSender;

import me.kev.sva.ServerAssistantPlugin;
import me.kev.sva.commands.CommandNode;
import me.kev.sva.commands.sva.listen.CNListener;

public class CNSva extends CommandNode {

  public CNSva(CommandSender sender, ServerAssistantPlugin plugin) {
    super(sender, plugin);
  }

  @Override
  public String getName() {
    return "sva";
  }

  @Override
  public List<CommandNode> getSubCommands() {
    return List.of(
        new CNListener(sender, plugin),
        new CNReload(sender, plugin));
  }

  @Override
  public boolean execute(List<String> args) {
    if (args.isEmpty()) {
      throwMissingArgumentsError();
      return false;
    }

    boolean subCommandsExecuted = executeSubcommands(args);
    if (subCommandsExecuted) {
      return true;
    }
    return false;
  }
}
