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
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;

public final class ServerAssistant extends JavaPlugin {

    private ConversationManager conversationManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        getCommand("sva").setExecutor(this);
        getCommand("sva").setTabCompleter(this);

        conversationManager = new ConversationManager(this);

        getServer().getPluginManager().registerEvents(
                new ChatListener(conversationManager),
                this);

        Bukkit.getConsoleSender().sendMessage(Constants.ASCII_LOGO);
        Bukkit.getConsoleSender().sendMessage(
                ChatColor.BLUE + "[Server Assistant] Plugin enabled successfully.");
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(
                ChatColor.RED + "[Server Assistant] Plugin Disabled!");
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {

            try {
                getLogger().info("Attempting to reload config...");

                reloadConfig();

                getLogger().info("Config reload successful.");

                sender.sendMessage(
                        Component.text("[Server Assistant] config reloaded."));

            } catch (Exception e) {

                getLogger().severe(
                        "Config reload failed: "
                                + e.getClass().getName()
                                + ": "
                                + e.getMessage());

                e.printStackTrace();

                sender.sendMessage(
                        Component.text(
                                "[Server Assistant] config reload failed. "
                                        + "Check console."));
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