package me.kev.sva.commands.sva;

import java.util.List;

import org.bukkit.command.CommandSender;

import me.kev.sva.ServerAssistantPlugin;
import me.kev.sva.commands.CommandNode;

public class CNTrigger extends CommandNode {

  protected CNTrigger(CommandSender sender, ServerAssistantPlugin plugin) {
    super(sender, plugin);
  }

  @Override
  public String getName() {
    return "trigger";
  }

  @Override
  public boolean execute(List<String> args) {
    if (!args.isEmpty()) {
      throwExtraArgumentsError();
      return false;
    }

    plugin.getConversationManager().getAssistantManager().sendAIRequest();
    return true;
  }
}
