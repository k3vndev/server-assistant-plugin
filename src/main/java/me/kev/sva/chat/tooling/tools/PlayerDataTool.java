package me.kev.sva.chat.tooling.tools;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import me.kev.sva.ServerAssistantPlugin;
import me.kev.sva.chat.tooling.ToolBase;

public class PlayerDataTool extends ToolBase {

  public PlayerDataTool(ServerAssistantPlugin plugin) {
    super(plugin);
  }

  @Override
  public String getCommand() {
    return "player-data";
  }

  @Override
  protected String getUsageDescription() {
    return """
        Retrieves information about an online player.

        Usage:
        player-data <player>

        Includes the player's location, world, game mode, health,
        hunger, experience, and other basic status information.

        Use the inventory tool separately when you need to inspect
        what items the player is carrying.
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
          "Invalid number of arguments. Usage: player-data <player>");
    }

    String playerName = args.get(1);
    Player player = Bukkit.getPlayerExact(playerName);

    if (player == null) {
      return wrapResult(
          "Player '" + playerName + "' is not online.");
    }

    return wrapResult(formatPlayerData(player));
  }

  private String formatPlayerData(Player player) {
    var location = player.getLocation();

    return """
        Player: %s
        UUID: %s

        LOCATION:
        World: %s
        X: %.2f
        Y: %.2f
        Z: %.2f
        Yaw: %.2f
        Pitch: %.2f

        STATUS:
        Game mode: %s
        Health: %.1f
        Food: %d / 20
        Saturation: %.1f
        Experience level: %d
        Experience progress: %.2f
        Flying: %s
        Sneaking: %s
        Sprinting: %s
        Swimming: %s
        Gliding: %s
        Invisible: %s
        """.formatted(
        player.getName(),
        player.getUniqueId(),
        player.getWorld().getName(),
        location.getX(),
        location.getY(),
        location.getZ(),
        location.getYaw(),
        location.getPitch(),
        formatGameMode(player.getGameMode()),
        player.getHealth(),
        player.getFoodLevel(),
        player.getSaturation(),
        player.getLevel(),
        player.getExp(),
        player.isFlying(),
        player.isSneaking(),
        player.isSprinting(),
        player.isSwimming(),
        player.isGliding(),
        player.isInvisible());
  }

  private String formatGameMode(GameMode gameMode) {
    return switch (gameMode) {
      case SURVIVAL -> "Survival";
      case CREATIVE -> "Creative";
      case ADVENTURE -> "Adventure";
      case SPECTATOR -> "Spectator";
    };
  }

}
