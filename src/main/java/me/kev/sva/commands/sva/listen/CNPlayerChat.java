package me.kev.sva.commands.sva.listen;

import java.util.List;

import org.bukkit.command.CommandSender;

import me.kev.sva.ServerAssistantPlugin;
import me.kev.sva.commands.CommandNode;
import me.kev.sva.utils.MessageSender;

public class CNPlayerChat extends CommandNode {

  public CNPlayerChat(CommandSender sender, ServerAssistantPlugin plugin) {
    super(sender, plugin);
  }

  @Override
  public String getName() {
    return "playerchat";
  }

  @Override
  public List<String> getOptions() {
    return List.of("always", "mention", "smart", "disabled");
  }

  @Override
  public boolean execute(List<String> args) {
    if (args.isEmpty()) {
      throwMissingArgumentsError();
      return false;
    }

    if (args.size() > 1) {
      throwExtraArgumentsError();
      return false;
    }

    String mode = args.getFirst();
    List<String> modes = getOptions();

    if (modes.contains(mode)) {
      plugin.getConfig().set(
          "request-triggers.player-messages.mode",
          mode);

      plugin.saveConfig();

      MessageSender.Success(
          sender,
          "Player message listener mode set to: " + mode);
      return true;
    }

    throwInvalidArgumentError();
    return false;
  }
}
