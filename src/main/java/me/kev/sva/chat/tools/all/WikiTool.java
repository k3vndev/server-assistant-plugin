package me.kev.sva.chat.tools.all;

import org.bukkit.configuration.ConfigurationSection;

import me.kev.sva.ServerAssistantPlugin;
import me.kev.sva.utils.MessageSender;

public class WikiTool extends Tool {

  public WikiTool(ServerAssistantPlugin plugin) {
    super(plugin, "wiki");
  }

  public String getIndex() {
    ConfigurationSection wiki = plugin.getConfig()
        .getConfigurationSection("advanced-context.wiki");

    if (wiki == null) {
      return "";
    }

    StringBuilder result = new StringBuilder("WIKI AVAILABLE:\n");

    for (String key : wiki.getKeys(false)) {
      ConfigurationSection section = wiki.getConfigurationSection(key);

      if (section == null) {
        continue;
      }

      String description = section.getString(
          "description",
          "No description available.");

      result.append("\n" + name + " ")
          .append(key)
          .append("\n")
          .append(description)
          .append("\n");
    }

    // MessageSender.Success(result.toString());
    return result.toString();
  }

  public String getWiki(String key) {
    ConfigurationSection wiki = plugin.getConfig()
        .getConfigurationSection("advanced-context.wiki");

    if (wiki == null) {
      return "No wiki sections are configured.";
    }

    ConfigurationSection section = wiki.getConfigurationSection(key);

    if (section == null) {
      return "Unknown wiki key: " + key;
    }

    // MessageSender.Success(section.getString("content", ""));
    return section.getString("content", "");
  }
}
