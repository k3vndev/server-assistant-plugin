package me.kev.sva.chat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.chat.completions.ChatCompletionCreateParams;

import me.kev.sva.utils.MessageSender;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;

public class AssistantManager {
  private final JavaPlugin plugin;
  private final OpenAIClient client;
  private final ConversationManager conversationManager;
  private volatile boolean shutdown = false;

  public AssistantManager(JavaPlugin plugin, ConversationManager conversationManager) {
    this.plugin = plugin;
    this.conversationManager = conversationManager;

    String apiKey = plugin.getConfig().getString("api-key");
    if (apiKey == null || apiKey.isBlank()) {
      // Handle api-key not configured
      MessageSender.Error("api-key not configured. AI features disabled.");
      client = null;
    } else {
      // Create client
      client = OpenAIOkHttpClient.builder()
          .apiKey(apiKey)
          .build();
    }
  }

  /**
   * Mark this assistant manager as shutdown so ongoing/queued work is skipped.
   */
  public void shutdown() {
    shutdown = true;
  }

  public void promptAIModel() {
    if (shutdown)
      return;
    String configuredModel = plugin.getConfig().getString("ai-model");

    if (configuredModel == null || configuredModel.isBlank()) {
      MessageSender.Error("ai-model is not configured.");
      return;
    }

    if (client == null) {
      MessageSender.Error(
          "Skipping AI request because API client is not configured.");
      return;
    }

    ChatCompletionCreateParams.Builder paramsBuilder = ChatCompletionCreateParams
        .builder()
        .model(configuredModel);

    appendSystemPromptsToBuilder(paramsBuilder);

    List<ChatMessage> chatMessages = conversationManager.getTrimmedConversation();
    appendConversationMessagesToBuilder(paramsBuilder, chatMessages);

    ChatCompletionCreateParams params = paramsBuilder.build();

    CompletableFuture
        .supplyAsync(() -> client.chat().completions().create(params))
        .thenAccept(response -> {

          if (shutdown)
            return;
          String text = response.choices()
              .get(0)
              .message()
              .content()
              .orElse("");

          if (text.isEmpty())
            return;

          String assistantMessageText = sanitizeAssistantMessage(text);

          ChatMessage assistantMessage = new ChatMessage(
              plugin.getConfig().getString(
                  "assistant-name",
                  "ServerAssistant"),
              true,
              assistantMessageText);

          if (shutdown)
            return;

          conversationManager.addChatMessage(assistantMessage);

          Bukkit.getScheduler().runTask(plugin, () -> {
            if (shutdown)
              return;
            plugin.getServer().broadcast(
                Component.text(
                    formatAssistantMessage(assistantMessageText)));
          });
        })
        .exceptionally(error -> {
          if (!shutdown) {
            MessageSender.Error(
                "AI request failed: " + error.getMessage());
          }
          return null;
        });
  }

  private void appendSystemPromptsToBuilder(
      ChatCompletionCreateParams.Builder paramsBuilder) {

    // Personality prompt
    String personalityPrompt = plugin.getConfig().getString(
        "prompt",
        "You are ServerAssistant, an AI assistant inside a Minecraft server.");

    paramsBuilder.addSystemMessage(personalityPrompt);

    // Server information
    int onlineCount = Bukkit.getOnlinePlayers().size();

    String onlinePlayers = Bukkit.getOnlinePlayers().stream()
        .map(player -> player.getName())
        .sorted()
        .collect(Collectors.joining(", "));

    LocalDateTime now = LocalDateTime.now();

    String serverContext = """
        SERVER DATA:

        Current time: %s
        Current date: %s
        Online players: %d
        Player names: %s
        """.formatted(
        now.format(DateTimeFormatter.ofPattern("HH:mm")),
        now.toLocalDate(),
        onlineCount,
        onlinePlayers.isEmpty() ? "none" : onlinePlayers);

    paramsBuilder.addSystemMessage(serverContext);

    // Max assistant message lenght
    int maxAssistantMessageLength = plugin.getConfig().getInt(
        "chat.max-assistant-message-length",
        250);

    paramsBuilder.addSystemMessage("""
        Maximum assistant message length: %d characters
        """.formatted(maxAssistantMessageLength));
  }

  private void appendConversationMessagesToBuilder(
      ChatCompletionCreateParams.Builder paramsBuilder,
      List<ChatMessage> chatMessages) {

    int maxPlayerMessageLength = plugin.getConfig().getInt(
        "chat.max-player-message-length",
        250);

    for (ChatMessage message : chatMessages) {
      if (message.isAssistant) {
        paramsBuilder.addAssistantMessage(message.content);
        continue;
      }

      String content = message.content;

      if (maxPlayerMessageLength > 0 && content.length() > maxPlayerMessageLength) {
        content = content.substring(0, maxPlayerMessageLength);
      }

      paramsBuilder.addUserMessage(
          "[" + message.senderName + "] " + content);
    }
  }

  private String formatAssistantMessage(String text) {
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
            .replace("%message%", text));
  }

  private String sanitizeAssistantMessage(String text) {
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
}
