package me.kev.sva.chat.message;

import org.bukkit.entity.Player;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class PlayerChatMessage extends ChatMessage {
  public final Player player;
  public final String header;

  public PlayerChatMessage(Player player, String content) {
    super(content);
    this.player = player;

    // Parse header
    String displayName = PlainTextComponentSerializer.plainText()
        .serialize(player.displayName());
    this.header = displayName + " > ";
  }
}
