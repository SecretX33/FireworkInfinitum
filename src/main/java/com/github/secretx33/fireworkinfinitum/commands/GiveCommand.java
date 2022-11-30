package com.github.secretx33.fireworkinfinitum.commands;

import com.github.secretx33.fireworkinfinitum.manager.FireworkManager;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;

import java.util.Collections;
import java.util.List;

public class GiveCommand implements CommandExecutor, TabCompleter {

    private final FireworkManager fireworkManager;

    public GiveCommand(JavaPlugin plugin, FireworkManager fireworkManager) {
        this.fireworkManager = fireworkManager;
        plugin.getCommand("fireworkinfinitum").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String alias, String[] strings) {
        if (!fireworkManager.canUseCommands(sender)) {
            // Command sender cannot use this command, just silently return
            return true;
        }

        if (strings.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + alias + " <player>");
            return true;
        }

        var player = Bukkit.getPlayerExact(strings[0]);
        if (player == null) {
            sender.sendMessage(ChatColor.RED + "Player with name " + strings[0] + " was not found");
            return true;
        }

        var firework = fireworkManager.createInfiniteFirework();
        var exceedent = player.getInventory().addItem(firework);

        if (!exceedent.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "Player " + strings[0] + " inventory is full, could not give the firework to him");
            return true;
        }

        player.sendMessage(ChatColor.GREEN + "Successfully gave player " + player.getName() + " a " + getItemName(firework) + ".");
        return true;
    }

    private String getItemName(ItemStack item) {
        var itemMeta = item.hasItemMeta() ? item.getItemMeta() : null;
        return itemMeta != null && itemMeta.hasDisplayName()
            ? itemMeta.getDisplayName()
            : WordUtils.capitalizeFully(item.getType().name().replace("_", " "));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String alias, String[] strings) {
        if (!fireworkManager.canUseCommands(sender) || strings.length > 1) {
            return Collections.emptyList();
        }
        var playerName = strings[0];

        return Bukkit.getOnlinePlayers().stream()
            .map(HumanEntity::getName)
            .filter(name -> StringUtil.startsWithIgnoreCase(name, playerName))
            .toList();
    }
}
