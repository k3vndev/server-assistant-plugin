package me.kev.sva;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.HandlerList;

import me.kev.sva.chat.ChatListener;
import me.kev.sva.chat.ConversationManager;
import me.kev.sva.chat.tooling.ToolManager;
import me.kev.sva.commands.CommandNodesManager;
import me.kev.sva.constants.Constants;
import me.kev.sva.utils.MessageSender;

public final class ServerAssistantPlugin extends JavaPlugin {

    private ConversationManager conversationManager;
    private ChatListener chatListener;
    private ToolManager toolManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        // Register command handler
        CommandNodesManager commandManager = new CommandNodesManager(this);
        if (getCommand("sva") != null) {
            getCommand("sva").setExecutor(commandManager);
            getCommand("sva").setTabCompleter(commandManager);
        }

        initializePlugin();

        Bukkit.getConsoleSender().sendMessage(Constants.ASCII_LOGO);
        MessageSender.Success("Plugin enabled successfully.");
    }

    void initializePlugin() {
        // If already initialized, shut down previous services and unregister listener
        if (conversationManager != null) {
            try {
                conversationManager.shutdown();
            } catch (Exception ignored) {
            }
            conversationManager = null;
        }

        if (chatListener != null) {
            try {
                HandlerList.unregisterAll(chatListener);
            } catch (Exception ignored) {
            }
            chatListener = null;
        }

        // Create new conversation manager (reads updated config) and register listener
        conversationManager = new ConversationManager(this);
        chatListener = new ChatListener(this, conversationManager);
        toolManager = new ToolManager(this);
        getServer().getPluginManager().registerEvents(chatListener, this);
    }

    public ConversationManager getConversationManager() {
        return conversationManager;
    }

    public ToolManager getToolManager() {
        return toolManager;
    }

    /**
     * Public reload helper used by the command handler to reload config and
     * reinitialize.
     */
    public void reloadPlugin() {
        reloadConfig();
        initializePlugin();
    }

    @Override
    public void onDisable() {
        // Shutdown services and unregister listeners
        if (conversationManager != null) {
            try {
                conversationManager.shutdown();
            } catch (Exception ignored) {
            }
            conversationManager = null;
        }

        if (chatListener != null) {
            try {
                HandlerList.unregisterAll(chatListener);
            } catch (Exception ignored) {
            }
            chatListener = null;
        }

        MessageSender.Error("Plugin Disabled!");
    }

}