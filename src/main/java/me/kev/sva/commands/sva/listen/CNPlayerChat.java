package me.kev.sva.commands.sva.listen;

import java.util.List;

import org.bukkit.command.CommandSender;

import me.kev.sva.ServerAssistantPlugin;
import me.kev.sva.commands.CommandNode;
import me.kev.sva.utils.MessageSender;

public class CNPlayerChat extends CommandNode {

  protected CNPlayerChat(CommandSender sender, ServerAssistantPlugin plugin) {
    super(sender, plugin);
  }

  @Override
  public String getName() {
    return "playerchat";
  }

  @Override
  public List<String> getCommandOptions() {
    return List.of("always", "mention", "smart", "disabled");
  }

  @Override
  public boolean execute(List<String> args) {
    if (args.isEmpty()) {
      throwMissingArgumentsError();
      return false;
    }

    String mode = args.getFirst();
    List<String> modes = getCommandOptions();

    if (modes.contains(mode)) {
      plugin.getConfig().set(
          "request-triggers.player-messages.mode",
          mode);

      plugin.saveConfig();

      MessageSender.Success(
          "Player message mode set to: " + mode);
      return true;
    }

    MessageSender.Error(
        "Invalid mode. Use: " + String.join(", ", modes) + ".");
    return false;
  }
}
