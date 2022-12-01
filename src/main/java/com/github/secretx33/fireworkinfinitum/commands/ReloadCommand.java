package com.github.secretx33.fireworkinfinitum.commands;

import com.github.secretx33.fireworkinfinitum.manager.FireworkManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.List;

public class ReloadCommand implements CommandExecutor, TabCompleter {

    private final Plugin plugin;
    private final FireworkManager fireworkManager;

    public ReloadCommand(JavaPlugin plugin, FireworkManager fireworkManager) {
        this.plugin = plugin;
        this.fireworkManager = fireworkManager;
        plugin.getCommand("fireworkinfinitumreload").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String alias, String[] strings) {
        if (!fireworkManager.canUseCommands(sender)) {
            // Command sender cannot use this command, just silently return
            return true;
        }

        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        fireworkManager.reload();
        sender.sendMessage(ChatColor.GREEN + "Reloaded plugin configs");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String alias, String[] strings) {
        return Collections.emptyList();
    }
}
