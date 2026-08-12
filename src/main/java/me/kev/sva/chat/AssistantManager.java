package me.kev.sva.chat;

import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.responses.ResponseCreateParams;
import com.openai.models.responses.ResponseOutputItem;

import me.kev.sva.utils.MessageSender;
import net.kyori.adventure.text.Component;

public class AssistantManager {
  final JavaPlugin plugin;
  private final OpenAIClient client;

  public AssistantManager(JavaPlugin plugin) {
    this.plugin = plugin;

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
    String configuredModel = plugin.getConfig().getString("ai-model");

    if (configuredModel == null || configuredModel.isBlank()) {
      MessageSender.Error("ai-model is not configured.");
      return;
    }

    if (client == null) {
      MessageSender.Error("Skipping AI request because API client is not configured.");
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
}
