package me.kev.sva.chat;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class ConversationManager {

  private final JavaPlugin plugin;
  private final List<ChatMessage> conversation = new ArrayList<>();

  private BukkitTask batchTask;

  public ConversationManager(JavaPlugin plugin) {
    this.plugin = plugin;
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

    // TODO: Send conversation to AI
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