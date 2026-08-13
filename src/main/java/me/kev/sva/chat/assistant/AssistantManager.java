package me.kev.sva.chat.assistant;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.chat.completions.ChatCompletionCreateParams;

import me.kev.sva.ServerAssistantPlugin;
import me.kev.sva.chat.ConversationManager;
import me.kev.sva.chat.message.AssistantChatMessage;
import me.kev.sva.chat.message.BroadcastChatMessage;
import me.kev.sva.chat.message.ChatMessage;
import me.kev.sva.chat.message.PlayerChatMessage;
import me.kev.sva.utils.MessageSender;

public class AssistantManager {
  private final ServerAssistantPlugin plugin;
  private final OpenAIClient client;
  private final ConversationManager conversationManager;
  private volatile boolean shutdown = false;

  public AssistantManager(ServerAssistantPlugin plugin, ConversationManager conversationManager) {
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

  public void sendAIRequest() {
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

          MessageSender.Success(text);

          // Create new assistant message
          AssistantChatMessage assistantMessage = new AssistantChatMessage(plugin, text);
          conversationManager.addChatMessage(assistantMessage);

          // Broadcast messages and call tools
          assistantMessage.response.broadcastMessages();
          assistantMessage.response.callTools();
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

    // Main primary prompt
    paramsBuilder.addSystemMessage(AssistantContextualizer.PRIMARY_SYSTEM_INSTRUCTIONS);

    // Personality prompt
    String personalityPrompt = plugin.getConfig().getString(
        "prompt",
        AssistantContextualizer.DEFAULT_PERSONALITY_PROMPT);

    paramsBuilder.addSystemMessage(AssistantContextualizer.PERSONALITY_PROMPT_HEADER + personalityPrompt);

    paramsBuilder.addSystemMessage(AssistantContextualizer.getServerContext());

    // Max assistant message length
    int maxAssistantMessageLength = plugin.getConfig().getInt(
        "chat.max-assistant-message-length",
        250);

    paramsBuilder.addSystemMessage("""
        Maximum assistant message length: %d characters
        """.formatted(maxAssistantMessageLength));

    // Avilable tools prompt
    paramsBuilder.addSystemMessage(AssistantContextualizer.getAvailableTools(plugin));
  }

  private void appendConversationMessagesToBuilder(
      ChatCompletionCreateParams.Builder paramsBuilder,
      List<ChatMessage> chatMessages) {

    int maxPlayerMessageLength = plugin.getConfig().getInt(
        "chat.max-player-message-length",
        250);

    for (ChatMessage message : chatMessages) {
      if (message instanceof AssistantChatMessage assistantMessage) {
        paramsBuilder.addAssistantMessage(assistantMessage.content);
        continue;
      }

      if (message instanceof PlayerChatMessage playerMessage) {
        String msg = message.content;

        if (maxPlayerMessageLength > 0 && msg.length() > maxPlayerMessageLength) {
          msg = msg.substring(0, maxPlayerMessageLength);
        }

        paramsBuilder.addSystemMessage(playerMessage.header + msg);
        continue;
      }

      if (message instanceof BroadcastChatMessage broadcastMessage) {
        paramsBuilder.addSystemMessage(broadcastMessage.header + broadcastMessage.content);
      }
    }
  }

}
