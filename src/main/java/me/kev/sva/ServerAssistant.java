package me.kev.sva;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;

import me.kev.sva.chat.ChatListener;
import me.kev.sva.chat.ConversationManager;
import me.kev.sva.constants.Constants;
import me.kev.sva.utils.MessageSender;
import net.kyori.adventure.text.Component;

public final class ServerAssistant extends JavaPlugin {

    private ConversationManager conversationManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        getCommand("sva").setExecutor(this);
        getCommand("sva").setTabCompleter(this);

        initializePlugin();

        getServer().getPluginManager().registerEvents(
                new ChatListener(conversationManager),
                this);

        Bukkit.getConsoleSender().sendMessage(Constants.ASCII_LOGO);
        MessageSender.Success("Plugin enabled successfully.");
    }

    void initializePlugin() {
        conversationManager = new ConversationManager(this);
    }

    @Override
    public void onDisable() {
        MessageSender.Error("Plugin Disabled!");
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {

            try {
                reloadConfig();
                initializePlugin();

                MessageSender.Success("Plugin reloaded!");

            } catch (Exception e) {

                getLogger().severe(
                        "Config reload failed: "
                                + e.getClass().getName()
                                + ": "
                                + e.getMessage());

                e.printStackTrace();

                MessageSender.Error("Config reload failed. Check console.");
            }

            return true;
        }

        sender.sendMessage(
                Component.text("Usage: /sva reload"));

        return true;
    }

    @Override
    public List<String> onTabComplete(
            CommandSender sender,
            Command command,
            String label,
            String[] args) {
        if (!command.getName().equalsIgnoreCase("sva")) {
            return Collections.emptyList();
        }

        if (args.length == 1 && sender.hasPermission("sva.reload")) {
            List<String> completions = new ArrayList<>();

            StringUtil.copyPartialMatches(
                    args[0],
                    List.of("reload"),
                    completions);

            return completions;
        }

        return Collections.emptyList();
    }
}