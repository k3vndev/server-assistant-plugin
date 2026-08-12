package me.kev.sva.chat;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class ConversationManager {

  private final JavaPlugin plugin;
  private final List<ChatMessage> conversation = new ArrayList<>();
  private final AssistantManager assistantManager;
  private BukkitTask batchTask;

  public ConversationManager(JavaPlugin plugin) {
    this.plugin = plugin;
    this.assistantManager = new AssistantManager(plugin, this);
  }

  public void shutdown() {
    if (batchTask != null) {
      try {
        batchTask.cancel();
      } catch (Exception ignored) {
      }
      batchTask = null;
    }

    if (assistantManager != null) {
      assistantManager.shutdown();
    }

    conversation.clear();
  }

  public void playerMessage(ChatMessage message) {
    addChatMessage(message);
    scheduleBatch();
  }

  private void scheduleBatch() {
    if (batchTask != null) {
      batchTask.cancel();
    }

    long delayMs = plugin.getConfig().getLong("message-batch-delay", 500);
    long delayTicks = Math.max(1, (delayMs + 49) / 50);

    batchTask = plugin.getServer().getScheduler().runTaskLater(
        plugin,
        this::processBatch,
        delayTicks);
  }

  private void processBatch() {
    batchTask = null;
    assistantManager.promptAIModel();
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

  public void addChatMessage(ChatMessage message) {
    conversation.add(message);
  }
}