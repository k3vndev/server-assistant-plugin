package me.kev.sva.chat.tooling.tools;

import java.util.List;
import java.util.Locale;

import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import me.kev.sva.ServerAssistantPlugin;
import me.kev.sva.chat.tooling.ToolBase;
import me.kev.sva.utils.MessageSender;

public class SoundTool extends ToolBase {

  public SoundTool(ServerAssistantPlugin plugin) {
    super(plugin);
  }

  @Override
  public String getCommand() {
    return "sound";
  }

  @Override
  protected String getUsageDescription() {
    ConfigurationSection sounds = plugin.getConfig()
        .getConfigurationSection("tools.sound.sounds");

    StringBuilder result = new StringBuilder("""
        Plays a sound from the server's curated sound list.

        Usage:
        sound <name>

        The name must exactly match one of the sounds listed below.

        Available sounds:
        """);

    if (sounds == null) {
      result.append("\nNo sounds are configured.");
      return result.toString();
    }

    boolean found = false;

    for (String name : sounds.getKeys(false)) {
      String soundName = sounds.getString(name, "");

      if (soundName.isBlank()) {
        continue;
      }

      found = true;

      result.append("\n- ")
          .append(name);
    }

    if (!found) {
      result.append("\nNo sounds are configured.");
    }

    return result.toString();
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
          "Invalid number of arguments. Usage: sound <name>");
    }

    String name = args.get(1);

    ConfigurationSection sounds = plugin.getConfig()
        .getConfigurationSection("tools.sound.sounds");

    if (sounds == null) {
      String message = "No sounds are configured on this server.";
      MessageSender.Dev(message);
      return wrapResult(message);
    }

    String soundName = sounds.getString(name, "");

    if (soundName.isBlank()) {
      return wrapResult(
          "Unknown sound: " + name
              + ". Use the available sound list to find valid names.");
    }

    Sound sound;

    try {
      sound = Sound.valueOf(
          soundName.toUpperCase(Locale.ROOT));
    } catch (IllegalArgumentException e) {
      String message = "Invalid Minecraft sound '" + soundName
          + "' configured for sound '" + name + "'.";

      plugin.getLogger().warning(message);
      return wrapResult(message);
    }

    for (Player player : plugin.getServer().getOnlinePlayers()) {
      player.playSound(
          player.getLocation(),
          sound,
          1.0f,
          1.0f);
    }

    return wrapResult(
        "Played sound '" + name + "' to all online players.");
  }
}