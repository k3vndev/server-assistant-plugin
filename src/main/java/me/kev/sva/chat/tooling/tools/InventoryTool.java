package me.kev.sva.chat.tooling.tools;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import me.kev.sva.ServerAssistantPlugin;
import me.kev.sva.chat.tooling.ToolBase;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class InventoryTool extends ToolBase {

  public InventoryTool(ServerAssistantPlugin plugin) {
    super(plugin);
  }

  @Override
  public String getCommand() {
    return "inventory";
  }

  @Override
  protected String getUsageDescription() {
    return """
        Retrieves the current inventory of an ONLINE player.

        Usage:
        inventory <player>

        The result includes the items currently carried by the player,
        their quantities, equipped armor, and off-hand item.

        Use this tool when you need to inspect what a player currently
        has or is carrying. Do not call it when the inventory information
        is not relevant to the player's request.
        """;
  }

  @Override
  public int getExpectedArgumentCount() {
    return 2;
  }

  @Override
  public String perform(String toolCall) {
    List<String> args = extractArguments(toolCall);

    if (!checkArgumentCount(args)) {
      return wrapResult(
          "Invalid number of arguments. Usage: inventory <player>");
    }

    String playerName = args.get(1);
    Player player = Bukkit.getPlayerExact(playerName);

    if (player == null) {
      return wrapResult(
          "Player '" + playerName + "' is not online.");
    }

    return wrapResult(formatInventory(player));
  }

  private String formatInventory(Player player) {
    PlayerInventory inventory = player.getInventory();

    Map<String, Integer> items = new LinkedHashMap<>();

    for (int slot = 0; slot < 36; slot++) {
      ItemStack item = inventory.getItem(slot);

      if (isEmpty(item)) {
        continue;
      }

      String name = getItemName(item);

      items.merge(
          name,
          item.getAmount(),
          Integer::sum);
    }

    StringBuilder result = new StringBuilder();

    result.append("Player: ")
        .append(player.getName())
        .append("\n\n");

    result.append("MAIN INVENTORY:\n");

    if (items.isEmpty()) {
      result.append("(empty)\n");
    } else {
      for (Map.Entry<String, Integer> entry : items.entrySet()) {
        result.append("- ")
            .append(entry.getKey())
            .append(" x")
            .append(entry.getValue())
            .append("\n");
      }
    }

    result.append("\nARMOR:\n");

    result.append("- Helmet: ")
        .append(formatItem(inventory.getHelmet()))
        .append("\n");

    result.append("- Chestplate: ")
        .append(formatItem(inventory.getChestplate()))
        .append("\n");

    result.append("- Leggings: ")
        .append(formatItem(inventory.getLeggings()))
        .append("\n");

    result.append("- Boots: ")
        .append(formatItem(inventory.getBoots()))
        .append("\n");

    result.append("\nOFF-HAND:\n");

    result.append("- ")
        .append(formatItem(inventory.getItemInOffHand()))
        .append("\n");

    return result.toString();
  }

  private String formatItem(ItemStack item) {
    if (isEmpty(item)) {
      return "(empty)";
    }

    return getItemName(item) + " x" + item.getAmount();
  }

  private String getItemName(ItemStack item) {
    Component displayName = item.getItemMeta().displayName();

    if (displayName != null) {
      return PlainTextComponentSerializer.plainText()
          .serialize(displayName);
    }

    return item.getType()
        .getKey()
        .toString()
        .replace("minecraft:", "");
  }

  private boolean isEmpty(ItemStack item) {
    return item == null || item.getType().isAir();
  }
}