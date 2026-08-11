package me.kev.sva;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import me.kev.sva.constants.Constants;
import net.md_5.bungee.api.ChatColor;

public final class ServerAssistant extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getCommand("sva").setExecutor(this);
        Bukkit.getConsoleSender().sendMessage(Constants.ASCII_LOGO);
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(
                ChatColor.RED + "[Server Assistant] Plugin Disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("sva")) {
            return false;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("sva.reload")) {
                sender.sendMessage("§cYou do not have permission to use this command.");
                return true;
            }

            reloadConfig();
            sender.sendMessage("§aReloaded config.yml.");
            return true;
        }

        sender.sendMessage("§cUsage: /sva reload");
        return true;
    }
}