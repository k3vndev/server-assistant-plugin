package me.kev.sva.chat;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class ConversationManager {

  private final JavaPlugin plugin;
  private final List<ChatMessage> conversation = new ArrayList<>();
  private final AssistantManager assistantManager;

  private int batchMessageCount = 0;
  private long batchStartTime = 0;
  private BukkitTask batchTask;
  private BukkitTask maxBatchTask;

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

    if (batchMessageCount == 0) {
      batchStartTime = System.currentTimeMillis();
      scheduleMaxBatchWait();
    }

    batchMessageCount++;

    int maxMessages = plugin.getConfig().getInt(
        "max-batch-messages",
        10);

    if (maxMessages > 0 && batchMessageCount >= maxMessages) {
      processBatch();
      return;
    }

    scheduleBatch();
  }

  private void scheduleBatch() {
    long now = System.currentTimeMillis();

    // Start a new batch if there isn't one already.
    if (batchStartTime == 0) {
      batchStartTime = now;
    }

    if (batchTask != null) {
      batchTask.cancel();
    }

    long delayMs = getMessageBatchDelayMs();
    long maxWaitMs = getMaxBatchWaitMs();

    long elapsed = now - batchStartTime;
    long remainingMaxWait = maxWaitMs <= 0
        ? delayMs
        : Math.max(0, maxWaitMs - elapsed);

    long actualDelay = Math.min(delayMs, remainingMaxWait);

    long delayTicks = Math.max(1, (actualDelay + 49) / 50);

    batchTask = plugin.getServer().getScheduler().runTaskLater(
        plugin,
        this::processBatch,
        delayTicks);
  }

  private void scheduleMaxBatchWait() {
    long maxWaitMs = getMaxBatchWaitMs();

    if (maxWaitMs <= 0) {
      return;
    }

    long delayTicks = Math.max(
        1,
        (maxWaitMs + 49) / 50);

    maxBatchTask = plugin.getServer().getScheduler().runTaskLater(
        plugin,
        this::processBatch,
        delayTicks);
  }

  private void processBatch() {
    if (batchTask != null) {
      batchTask.cancel();
      batchTask = null;
    }

    if (maxBatchTask != null) {
      maxBatchTask.cancel();
      maxBatchTask = null;
    }

    batchMessageCount = 0;
    batchStartTime = 0;

    assistantManager.promptAIModel();
  }

  public List<ChatMessage> getConversation() {
    return List.copyOf(conversation);
  }

  public List<ChatMessage> getTrimmedConversation() {
    List<ChatMessage> conversation = getConversation();

    int limit = getMessageHistoryLimit();

    if (limit <= 0 || conversation.size() <= limit) {
      return conversation;
    }

    int start = conversation.size() - limit;

    return conversation.subList(start, conversation.size());
  }

  public void addChatMessage(ChatMessage message) {
    conversation.add(message);
  }

  private long getMessageBatchDelayMs() {
    return plugin.getConfig().getLong("message-batch-delay", 500);
  }

  private long getMaxBatchWaitMs() {
    return plugin.getConfig().getLong("max-batch-wait", 10000);
  }

  private int getMessageHistoryLimit() {
    return plugin.getConfig().getInt("message-history-limit", 15);
  }
}