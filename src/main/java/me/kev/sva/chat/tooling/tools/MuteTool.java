package me.kev.sva.chat.tooling.tools;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.kev.sva.ServerAssistantPlugin;
import me.kev.sva.chat.tooling.ToolBase;

public class MuteTool extends ToolBase {

  private static final String DURATION = "5m";

  public MuteTool(ServerAssistantPlugin plugin) {
    super(plugin);
  }

  @Override
  public String getCommand() {
    return "mute";
  }

  @Override
  protected String getUsageDescription() {
    return """
        Mutes an online player for a fixed duration of 5 minutes.

        Usage:
        mute <player>

        Only use this tool when muting the player is appropriate.
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
          "Invalid number of arguments. Usage: mute <player>");
    }

    String playerName = args.get(1);
    Player player = Bukkit.getPlayerExact(playerName);

    if (player == null) {
      return wrapResult(
          "Player '" + playerName + "' is not online.");
    }

    String command = "mute " + player.getName() + " " + DURATION;

    boolean success = Bukkit.dispatchCommand(
        Bukkit.getConsoleSender(),
        command);

    if (!success) {
      return wrapResult(
          "Failed to mute player '" + player.getName() + "'.");
    }

    return wrapResult(
        "Muted " + player.getName() + " for 5 minutes.");
  }
}