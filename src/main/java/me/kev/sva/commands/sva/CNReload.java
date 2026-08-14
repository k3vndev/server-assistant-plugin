package me.kev.sva.commands.sva;

import java.util.List;

import org.bukkit.command.CommandSender;

import me.kev.sva.ServerAssistantPlugin;
import me.kev.sva.commands.CommandNode;
import me.kev.sva.utils.MessageSender;

/**
 * Handles the /sva reload command.
 */
public class CNReload extends CommandNode {
  private final ServerAssistantPlugin plugin;

  public CNReload(CommandSender sender, ServerAssistantPlugin plugin) {
    super(sender, plugin);
    this.plugin = plugin;
  }

  @Override
  public String getName() {
    return "reload";
  }

  @Override
  public boolean execute(List<String> args) {
    try {
      plugin.reloadPlugin();
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
}
