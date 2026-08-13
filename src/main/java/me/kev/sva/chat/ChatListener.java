package me.kev.sva.chat;

import java.util.List;
import java.util.Locale;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.kev.sva.chat.message.BroadcastChatMessage;
import me.kev.sva.chat.message.PlayerChatMessage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class ChatListener implements Listener {

  private final ConversationManager conversationManager;
  private final JavaPlugin plugin;

  public ChatListener(JavaPlugin plugin, ConversationManager conversationManager) {
    this.plugin = plugin;
    this.conversationManager = conversationManager;
  }

  @EventHandler
  public void onChat(AsyncChatEvent event) {
    Player player = event.getPlayer();

    String message = PlainTextComponentSerializer.plainText()
        .serialize(event.message());

    if (!shouldProcessPlayerMessage(message)) {
      return;
    }

    conversationManager.queueNewMessage(
        new PlayerChatMessage(plugin, player, message));
  }

  private boolean shouldProcessPlayerMessage(String message) {
    FileConfiguration config = plugin.getConfig();

    String mode = config.getString(
        "request-triggers.player-messages.mode",
        "mention");

    return switch (mode.toLowerCase(Locale.ROOT)) {
      case "always" -> true;

      case "mention" -> {
        List<String> mentions = config.getStringList(
            "request-triggers.player-messages.mentions");

        String lowerMessage = message.toLowerCase(Locale.ROOT);

        yield mentions.stream()
            .map(mention -> mention.toLowerCase(Locale.ROOT))
            .anyMatch(lowerMessage::contains);
      }

      case "disabled" -> false;

      default -> {
        plugin.getLogger().warning(
            "Unknown player message trigger mode: " + mode);

        yield false;
      }
    };
  }

  // ------------------------------------------------------------
  // GLOBAL EVENTS
  // ------------------------------------------------------------

  private boolean shouldProcessGlobalEvent(String eventName) {
    FileConfiguration config = plugin.getConfig();

    if (!config.getBoolean(
        "request-triggers.global-events.enabled",
        true)) {

      return false;
    }

    return config.getBoolean(
        "request-triggers.global-events.events." + eventName,
        false);
  }

  private void queueGlobalEvent(String message) {
    if (message == null || message.isBlank()) {
      return;
    }

    conversationManager.queueNewMessage(
        new BroadcastChatMessage(plugin, message));
  }

  private String plain(Component component) {
    return PlainTextComponentSerializer.plainText()
        .serialize(component);
  }

  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event) {
    if (!shouldProcessGlobalEvent("player-death")) {
      return;
    }

    Component message = event.deathMessage();

    if (message == null) {
      return;
    }

    queueGlobalEvent(plain(message));
  }

  @EventHandler
  public void onPlayerAdvancement(PlayerAdvancementDoneEvent event) {
    if (!shouldProcessGlobalEvent("player-advancement")) {
      return;
    }

    Component message = event.message();

    if (message == null) {
      return;
    }

    queueGlobalEvent(plain(message));
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    if (!shouldProcessGlobalEvent("player-join")) {
      return;
    }

    Component message = event.joinMessage();

    if (message == null) {
      return;
    }

    queueGlobalEvent(plain(message));
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    if (!shouldProcessGlobalEvent("player-quit")) {
      return;
    }

    Component message = event.quitMessage();

    if (message == null) {
      return;
    }

    queueGlobalEvent(plain(message));
  }

  @EventHandler
  public void onPlayerKick(PlayerKickEvent event) {
    if (!shouldProcessGlobalEvent("player-kick")) {
      return;
    }

    Component message = event.leaveMessage();

    if (message == null) {
      return;
    }

    queueGlobalEvent(plain(message));
  }
}