package me.kev.sva.chat;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class ChatListener implements Listener {

  private final ConversationManager conversationManager;

  public ChatListener(ConversationManager conversationManager) {
    this.conversationManager = conversationManager;
  }

  @EventHandler
  public void onChat(AsyncChatEvent event) {
    Player player = event.getPlayer();

    String message = PlainTextComponentSerializer.plainText()
        .serialize(event.message());

    conversationManager.playerMessage(
        new ChatMessage(
            player.getName(),
            false,
            message));
  }
}