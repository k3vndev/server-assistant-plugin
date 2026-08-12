package me.kev.sva.chat;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.responses.ResponseCreateParams;
import com.openai.models.responses.ResponseOutputItem;

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ConversationManager {

  private final JavaPlugin plugin;
  private final List<ChatMessage> conversation = new ArrayList<>();
  private final OpenAIClient client;

  private BukkitTask batchTask;

  public ConversationManager(JavaPlugin plugin) {
    this.plugin = plugin;

    String apiKey = plugin.getConfig().getString("api-key");
    if (apiKey == null || apiKey.isBlank()) {
      Bukkit.getConsoleSender().sendMessage(
          ChatColor.RED + "[ServerAssistant] Warning: api-key not configured. AI features disabled.");
      client = null;
    } else {
      client = OpenAIOkHttpClient.builder()
          .apiKey(apiKey)
          .build();
    }
  }

  public void playerMessage(ChatMessage message) {
    conversation.add(message);

    scheduleBatch();
  }

  private void scheduleBatch() {
    if (batchTask != null) {
      batchTask.cancel();
    }

    long delayMs = plugin.getConfig().getLong("message-batch-delay", 1000);
    long delayTicks = Math.max(1, (delayMs + 49) / 50);

    batchTask = plugin.getServer().getScheduler().runTaskLater(
        plugin,
        this::processBatch,
        delayTicks);
  }

  private void processBatch() {
    batchTask = null;

    String configuredModel = plugin.getConfig().getString("ai-model");

    if (configuredModel == null || configuredModel.isBlank()) {
      Bukkit.getConsoleSender().sendMessage(
          ChatColor.RED + "[ServerAssistant] Error: ai-model is not configured.");
      return;
    }

    if (client == null) {
      Bukkit.getConsoleSender().sendMessage(
          ChatColor.RED + "[ServerAssistant] Skipping AI request because API client is not configured.");
      return;
    }

    ResponseCreateParams params = ResponseCreateParams.builder()
        .input("Hello!")
        .model(configuredModel)
        .build();

    CompletableFuture
        .supplyAsync(() -> client.responses().create(params))
        .thenAccept(response -> {

          StringBuilder output = new StringBuilder();

          for (ResponseOutputItem item : response.output()) {
            item.message().ifPresent(message -> {
              for (var content : message.content()) {
                content.outputText().ifPresent(text -> output.append(text.text()));
              }
            });
          }

          String text = output.toString();

          Bukkit.getScheduler().runTask(plugin, () -> {
            plugin.getServer().broadcast(
                Component.text("🤖 ServerAssistant: " + text));
          });
        });
  }

  public List<ChatMessage> getConversation() {
    return List.copyOf(conversation);
  }

  public List<ChatMessage> getTrimmedConversation() {
    List<ChatMessage> conversation = getConversation();

    int limit = plugin.getConfig().getInt("message-history-limit", 20);

    if (limit <= 0 || conversation.size() <= limit) {
      return conversation;
    }

    int start = conversation.size() - limit;

    return conversation.subList(start, conversation.size());
  }
}