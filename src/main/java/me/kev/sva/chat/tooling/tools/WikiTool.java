package me.kev.sva.chat.tooling.tools;

import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import me.kev.sva.ServerAssistantPlugin;
import me.kev.sva.chat.tooling.ToolBase;
import me.kev.sva.utils.MessageSender;

public class WikiTool extends ToolBase {

  public WikiTool(ServerAssistantPlugin plugin) {
    super(plugin);
  }

  @Override
  public String getCommand() {
    return "wiki";
  }

  @Override
  protected String getUsageDescription() {
    ConfigurationSection wiki = plugin.getConfig()
        .getConfigurationSection("tools.wiki.pages");

    if (wiki == null) {
      return "";
    }

    String usage = """
        Retrieves detailed information from a specific wiki section
        configured by the server administrator.

        Usage:
        wiki <key>

        The key must exactly match one of the keys listed in the WIKI INDEX below.
        Do not invent, modify, or guess wiki keys.

        Use the wiki when a player's question requires specific server
        information that may be stored there. Do not call it unnecessarily
        when you already have enough information to answer.

        The wiki is the server's source of truth for its configured rules,
        commands, economy, items, locations, mechanics, and other documented
        information.

        If the required information is not available in the wiki, do not
        invent it. You may answer using other available context when appropriate,
        but make it clear when you do not know something specific about the server.
        """;

    StringBuilder result = new StringBuilder(usage);
    result.append("\n\nWIKI AVAILABLE:\n\n");
    boolean keysFound = false;

    for (String key : wiki.getKeys(false)) {
      ConfigurationSection section = wiki.getConfigurationSection(key);

      if (section == null) {
        continue;
      }

      keysFound = true;

      String description = section.getString(
          "description",
          "No description available.");

      result.append("\n" + getCommand() + " " + key)
          .append("\n")
          .append(description)
          .append("\n");
    }

    if (!keysFound) {
      result.append("No ");
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
          "Invalid number of arguments. Usage: wiki <key>");
    }

    ConfigurationSection wiki = plugin.getConfig()
        .getConfigurationSection("tools.wiki.pages");

    if (wiki == null) {
      String message = "No wiki pages are configured on this server.";
      MessageSender.Dev(message);
      return wrapResult(message);
    }

    String key = args.get(1);
    ConfigurationSection section = wiki.getConfigurationSection(key);

    if (section == null) {
      return wrapResult(
          "Unknown wiki key: " + key
              + ". Use the WIKI INDEX to find available keys.");
    }

    String content = section.getString("content", "");

    if (content.isBlank()) {
      return wrapResult(
          "Wiki page '" + key + "' exists but has no content.");
    }

    return wrapResult(content);
  }
}
