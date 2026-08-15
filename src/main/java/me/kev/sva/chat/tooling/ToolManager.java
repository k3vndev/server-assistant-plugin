package me.kev.sva.chat.tooling;

import java.util.List;

import me.kev.sva.ServerAssistantPlugin;
import me.kev.sva.chat.tooling.tools.InventoryTool;
import me.kev.sva.chat.tooling.tools.LightningTool;
import me.kev.sva.chat.tooling.tools.MuteTool;
import me.kev.sva.chat.tooling.tools.PlayerDataTool;
import me.kev.sva.chat.tooling.tools.SoundTool;
import me.kev.sva.chat.tooling.tools.WikiTool;

public class ToolManager {
  protected final ServerAssistantPlugin plugin;

  public ToolManager(ServerAssistantPlugin plugin) {
    this.plugin = plugin;
  }

  public List<ToolBase> getAvailableTools() {
    List<ToolBase> tools = List.of(
        new WikiTool(plugin),
        new InventoryTool(plugin),
        new PlayerDataTool(plugin),
        new LightningTool(plugin),
        new SoundTool(plugin),
        new MuteTool(plugin)
    //
    );

    return tools.stream()
        .filter(ToolBase::canBeUsed)
        .toList();
  }
}
