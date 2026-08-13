package me.kev.sva.chat.message;

import org.bukkit.entity.Player;

import me.kev.sva.ServerAssistantPlugin;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class PlayerChatMessage extends ChatMessage {
  public final Player player;
  public final String header;

  public PlayerChatMessage(ServerAssistantPlugin plugin, Player player, String content) {
    super(plugin, content);
    this.player = player;

    // Parse header
    String displayName = PlainTextComponentSerializer.plainText()
        .serialize(player.displayName());
    this.header = displayName + " > ";
  }
}
