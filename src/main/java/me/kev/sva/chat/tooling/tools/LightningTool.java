package me.kev.sva.chat.tooling.tools;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.kev.sva.ServerAssistantPlugin;
import me.kev.sva.chat.tooling.ToolBase;

public class LightningTool extends ToolBase {

  public LightningTool(ServerAssistantPlugin plugin) {
    super(plugin);
  }

  @Override
  public String getCommand() {
    return "lightning";
  }

  @Override
  protected String getUsageDescription() {
    return """
        Creates a harmless lightning effect at an online player's location.

        Usage:
        lightning <player>

        The effect is purely visual and does not deal damage,
        ignite blocks, or create a real lightning strike.
        """;
  }

  @Override
  public int getExpectedArgumentCount() {
    return 2;
  }

  @Override
  public String perform(String toolCall) {
    List<String> args = extractArguments(toolCall);

    if (!checkArgumentCount(args)) {
      return wrapResult(
          "Invalid number of arguments. Usage: lightning <player>");
    }

    String playerName = args.get(1);
    Player player = Bukkit.getPlayerExact(playerName);

    if (player == null) {
      return wrapResult(
          "Player '" + playerName + "' is not online.");
    }

    plugin.getServer().getScheduler().runTask(
        plugin,
        () -> player.getWorld().strikeLightningEffect(player.getLocation()));

    return wrapResult(
        "Created a harmless lightning effect at " + player.getName() + "'s location.");
  }
}