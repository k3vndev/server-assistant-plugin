package me.kev.sva.chat.assistant;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import me.kev.sva.ServerAssistantPlugin;
import me.kev.sva.chat.ConversationManager;
import me.kev.sva.chat.message.BroadcastChatMessage;
import me.kev.sva.chat.tooling.ToolBase;
import me.kev.sva.chat.tooling.tools.WikiTool;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;

public class AssistantResponse {
  public final String raw;
  private final List<String> messages;
  private final List<String> toolCalls;
  private final ServerAssistantPlugin plugin;

  public AssistantResponse(ServerAssistantPlugin plugin, String response) {
    this.plugin = plugin;

    raw = response;
    Yaml yaml = new Yaml();

    Map<String, Object> data = yaml.load(response);

    this.messages = getStringList(data, "messages")
        .stream()
        .map(AssistantResponse::sanitizeMessage)
        .toList();

    this.toolCalls = getStringList(data, "tool-calls");
  }

  public AssistantResponse(
      ServerAssistantPlugin plugin,
      List<String> messages,
      List<String> toolCalls) {

    this.plugin = plugin;
    this.messages = List.copyOf(messages);
    this.toolCalls = List.copyOf(toolCalls);

    this.raw = toYaml(this.messages, this.toolCalls);
  }

  public List<String> getMessages() {
    return List.copyOf(messages);
  }

  public List<String> getToolCalls() {
    return List.copyOf(toolCalls);
  }

  private List<String> getStringList(
      Map<String, Object> data,
      String key) {

    Object value = data.get(key);

    if (!(value instanceof List<?> list)) {
      return List.of();
    }

    return list.stream()
        .filter(String.class::isInstance)
        .map(String.class::cast)
        .toList();
  }

  private String toYaml(
      List<String> messages,
      List<String> toolCalls) {

    Map<String, Object> data = new LinkedHashMap<>();

    data.put("messages", messages);
    data.put("tool-calls", toolCalls);

    DumperOptions options = new DumperOptions();

    options.setDefaultFlowStyle(
        DumperOptions.FlowStyle.BLOCK);

    options.setDefaultScalarStyle(
        DumperOptions.ScalarStyle.DOUBLE_QUOTED);

    Yaml yaml = new Yaml(options);

    return yaml.dump(data);
  }

  public static String formatMessage(ServerAssistantPlugin plugin, String message) {
    String assistantName = plugin.getConfig().getString(
        "assistant-name",
        "ServerAssistant");

    String format = plugin.getConfig().getString(
        "chat.assistant-format",
        "&b🤖 &b&l%assistant_name%: &r%message%");

    return ChatColor.translateAlternateColorCodes(
        '&',
        format
            .replace("%assistant_name%", assistantName)
            .replace("%message%", message));
  }

  private static String sanitizeMessage(String text) {
    if (text == null || text.isEmpty()) {
      return "";
    }

    return text.replaceAll(
        "[\\x{1F000}-\\x{1FAFF}" +
            "\\x{2600}-\\x{27BF}" +
            "\\x{2300}-\\x{23FF}" +
            "\\x{2B00}-\\x{2BFF}" +
            "\\x{FE00}-\\x{FE0F}" +
            "\\x{1F1E6}-\\x{1F1FF}]",
        "");
  }

  /**
   * Broadcasts all messages in this response to global chat.
   * Adds delay if configured.
   */
  public void broadcastMessages() {

    long delayMs = Math.max(
        plugin.getConfig().getLong(
            "chat.assistant-chained-messages-delay",
            750),
        0);

    long delayTicks = Math.max(
        1,
        (delayMs + 49) / 50);

    for (int i = 0; i < messages.size(); i++) {
      String message = messages.get(i);

      long ticks = delayTicks * i;

      plugin.getServer().getScheduler().runTaskLater(
          plugin,
          () -> {
            plugin.getServer().broadcast(
                Component.text(
                    formatMessage(plugin, message)));
          },
          ticks);
    }
  }

  /**
   * Executes all tools requested by the assistant and sends their results
   * back into the conversation for the next AI request.
   */
  public void callTools() {
    List<String> toolCalls = getToolCalls();

    if (toolCalls.isEmpty()) {
      return;
    }

    List<ToolBase> tools = List.of(
        new WikiTool(plugin)
    // Add new tools here.
    );

    StringBuilder toolResults = new StringBuilder();

    for (String toolCall : toolCalls) {
      ToolBase tool = tools.stream()
          .filter(t -> t.check(toolCall))
          .findFirst()
          .orElse(null);

      if (tool == null) {
        continue;
      }

      toolResults.append(tool.perform(toolCall))
          .append("\n");
    }

    // Add messages and call AI model
    ConversationManager conversationManager = plugin.getConversationManager();

    conversationManager.addChatMessage(
        new BroadcastChatMessage(plugin, toolResults.toString()));

    AssistantManager assistantManager = conversationManager.getAssistantManager();

    assistantManager.sendAIRequest();
  }
}