package me.kev.sva.commands.sva.listen.events;

import java.util.List;

import org.bukkit.command.CommandSender;

import me.kev.sva.ServerAssistantPlugin;
import me.kev.sva.commands.CommandNode;
import me.kev.sva.utils.MessageSender;

public class EventsCommandNode extends CommandNode {

  public final String pathName;
  public final String name;

  protected EventsCommandNode(CommandSender sender, ServerAssistantPlugin plugin, String pathName, String name) {
    super(sender, plugin);
    this.pathName = pathName;
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public List<String> getOptions() {
    return List.of("enabled", "disabled");
  }

  @Override
  public boolean execute(List<String> args) {
    boolean setOption = true;

    if (args.size() > 1) {
      throwExtraArgumentsError();
      return false;
    }

    if (args.size() == 1) {
      String first = args.getFirst();

      switch (first) {
        case "enabled":
          setOption = true;
          break;
        case "disabled":
          setOption = false;
          break;
        default:
          throwInvalidArgumentError();
          return false;
      }
    }

    boolean globalEventsAreEnabled = plugin.getConfig().getBoolean(
        "request-triggers.global-events.enabled", true);

    String propertyPath = "request-triggers.global-events.events." + pathName;
    plugin.getConfig().set(propertyPath, true);
    plugin.saveConfig();

    MessageSender.Success(sender, propertyPath + " set to " + setOption);

    if (!globalEventsAreEnabled && setOption) {
      MessageSender.Success(
          sender,
          "Warning! Global events are disabled in config, this listener will be ignored. Check config.yml and reload the plugin.");
    }
    return true;
  }
}
