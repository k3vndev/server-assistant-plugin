package me.kev.sva.chat;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.chat.completions.ChatCompletionCreateParams;

import me.kev.sva.utils.MessageSender;
import net.kyori.adventure.text.Component;

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

    List<ChatMessage> chatMessages = conversationManager.getTrimmedConversation();

    appendMessagesToBuilder(paramsBuilder, chatMessages);

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

          ChatMessage assistantMessage = new ChatMessage(
              plugin.getConfig().getString(
                  "assistant-name",
                  "ServerAssistant"),
              true,
              text);

          if (shutdown)
            return;

          conversationManager.addChatMessage(assistantMessage);

          Bukkit.getScheduler().runTask(plugin, () -> {
            if (shutdown)
              return;
            plugin.getServer().broadcast(
                Component.text(
                    formatAssistantMessage(text)));
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

  /**
   * Mark this assistant manager as shutdown so ongoing/queued work is skipped.
   */
  public void shutdown() {
    shutdown = true;
  }

  private void appendMessagesToBuilder(
      ChatCompletionCreateParams.Builder paramsBuilder,
      List<ChatMessage> chatMessages) {

    for (ChatMessage message : chatMessages) {
      if (message.isAssistant) {
        paramsBuilder.addAssistantMessage(message.content);
        continue;
      }

      paramsBuilder.addUserMessage(message.senderName + ": " + message.content);
    }
  }

  private String formatAssistantMessage(String text) {
    return "🤖 ServerAssistant: " + text;
  }
}
