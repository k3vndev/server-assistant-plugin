package me.kev.sva.custom_commands.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import me.kev.sva.ServerAssistantPlugin;
import me.kev.sva.custom_commands.CustomCommand;
import me.kev.sva.utils.MessageSender;

/**
 * Handles the /sva reload command.
 */
public class CustomCommandReload extends CustomCommand {
  private final ServerAssistantPlugin plugin;

  public CustomCommandReload(CommandSender sender, ServerAssistantPlugin plugin) {
    super(sender);
    this.plugin = plugin;
  }

  @Override
  public String getName() {
    return "reload";
  }

  @Override
  public boolean matches(String[] args) {
    return args.length == 1 && args[0].equalsIgnoreCase(getName());
  }

  @Override
  public boolean execute(String[] args) {
    try {
      if (plugin instanceof ServerAssistantPlugin) {
        ((ServerAssistantPlugin) plugin).reloadPlugin();
      } else {
        plugin.reloadConfig();
      }

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

  @Override
  public List<String> getTabCompletions(String[] args) {
    return List.of();
  }
}
