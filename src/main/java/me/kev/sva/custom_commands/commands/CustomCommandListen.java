package me.kev.sva.custom_commands.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import me.kev.sva.ServerAssistantPlugin;
import me.kev.sva.custom_commands.CustomCommand;
import me.kev.sva.utils.MessageSender;

// > /sva listen playerchat always|mention|smart|disabled
// > /sva listen playerchat -> defaults to "always"
// Scalable for future: /sva listen voicechat <mode>, /sva listen ... etc

/**
 * Handles the /sva listen command with support for multiple subcommands.
 * Structure: /sva listen <subcommand> [mode]
 */
public class CustomCommandListen extends CustomCommand {
  private static final String COMMAND_NAME = "listen";
  private static final String DEFAULT_MODE = "always";

  // Supported listening targets (subcommands)
  private static final List<String> VALID_SUBCOMMANDS = List.of("playerchat");
  private static final List<String> VALID_MODES = List.of("always", "mention", "smart", "disabled");

  private final ServerAssistantPlugin plugin;

  public CustomCommandListen(CommandSender sender, ServerAssistantPlugin plugin) {
    super(sender);
    this.plugin = plugin;
  }

  @Override
  public String getName() {
    return COMMAND_NAME;
  }

  @Override
  public boolean matches(String[] args) {
    // /sva listen <subcommand> [mode]
    return args.length >= 2 && args[0].equalsIgnoreCase(COMMAND_NAME)
        && isValidSubcommand(args[1]);
  }

  @Override
  public boolean execute(String[] args) {
    String subcommand = args[1].toLowerCase(Locale.ROOT);
    String mode = args.length >= 3 ? args[2].toLowerCase(Locale.ROOT) : DEFAULT_MODE;

    if (!VALID_MODES.contains(mode)) {
      MessageSender.Error(
          "Invalid mode. Use: " + String.join(", ", VALID_MODES) + ".");
      return true;
    }

    // Route to the appropriate subcommand handler
    switch (subcommand) {
      case "playerchat":
        return executePlayerChat(mode);
      default:
        MessageSender.Error("Unknown listen subcommand: " + subcommand);
        return true;
    }
  }

  /**
   * Handles the "playerchat" listening subcommand.
   */
  private boolean executePlayerChat(String mode) {
    plugin.getConfig().set(
        "request-triggers.player-messages.mode",
        mode);

    plugin.saveConfig();

    MessageSender.Success(
        "Player chat mode set to: " + mode);

    return true;
  }

  @Override
  public List<String> getTabCompletions(String[] args) {
    if (args.length == 1) {
      // First argument (after "listen") should be a subcommand
      return List.of();
    }

    if (args.length == 2 && args[0].equalsIgnoreCase(COMMAND_NAME)) {
      // Complete subcommand names
      List<String> matches = new ArrayList<>();
      StringUtil.copyPartialMatches(args[1], VALID_SUBCOMMANDS, matches);
      return matches;
    }

    if (args.length == 3 && args[0].equalsIgnoreCase(COMMAND_NAME)
        && isValidSubcommand(args[1])) {
      // Complete mode options for the selected subcommand
      List<String> matches = new ArrayList<>();
      StringUtil.copyPartialMatches(args[2], VALID_MODES, matches);
      return matches;
    }

    return List.of();
  }

  /**
   * Checks if a subcommand is valid.
   */
  private boolean isValidSubcommand(String subcommand) {
    return VALID_SUBCOMMANDS.contains(subcommand.toLowerCase(Locale.ROOT));
  }
}
