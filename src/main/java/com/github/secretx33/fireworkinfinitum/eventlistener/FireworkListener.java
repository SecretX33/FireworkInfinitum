package com.github.secretx33.fireworkinfinitum.eventlistener;

import com.github.secretx33.fireworkinfinitum.manager.FireworkManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class FireworkListener implements Listener {

    private final FireworkManager fireworkManager;

    public FireworkListener(FireworkManager fireworkManager) {
        this.fireworkManager = fireworkManager;
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        var player = event.getPlayer();
        var itemInMainHand = player.getInventory().getItemInMainHand();

        if (!fireworkManager.isInfiniteFirework(itemInMainHand)) {
            // Not an infinite firework, let's just return
            return;
        }

        if (fireworkManager.isPlayerFireworkOnCooldown(player)) {
            // Infinite firework is on cooldown, prevent its usage and return
            event.setCancelled(true);
            return;
        }

        if (!fireworkManager.hasFireworkCooldownBypass(player)) {
            fireworkManager.setPlayerFireworkOnCooldown(player);
        }

        // Makes the firework infinite by artificially increasing the number of
        // Fireworks in the player's inventory by 1
        itemInMainHand.setAmount(itemInMainHand.getAmount() + 1);
    }

}
