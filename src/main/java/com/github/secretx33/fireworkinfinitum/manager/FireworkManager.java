package com.github.secretx33.fireworkinfinitum.manager;

import com.cryptomorin.xseries.XItemStack;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.Math.max;

public class FireworkManager {

    private static final PersistentDataType<Byte, Byte> INFINITE_FIREWORK_PDC_TYPE = PersistentDataType.BYTE;

    private final Plugin plugin;
    private final NamespacedKey infiniteFireworkKey;
    private Cache<UUID, Instant> fireworkCooldownCache;
    private Duration cooldown;

    public FireworkManager(Plugin plugin) {
        this.plugin = plugin;
        this.infiniteFireworkKey = new NamespacedKey(plugin, "infinite_firework");
        reload();
    }

    public void reload() {
        cooldown = Duration.ofMillis(Math.round(plugin.getConfig().getDouble("firework-cooldown-in-seconds") * 1000));
        fireworkCooldownCache = CacheBuilder.newBuilder()
            .expireAfterWrite(cooldown)
            .build();
    }

    public ItemStack createInfiniteFirework() {
        var firework = createBaseFirework();
        var itemMeta = (FireworkMeta)checkNotNull(firework.getItemMeta(), "firework meta cannot be null");
        var pdc = itemMeta.getPersistentDataContainer();

        // The actual type does not matter since we will only use this to check if it is present or not
        pdc.set(infiniteFireworkKey, INFINITE_FIREWORK_PDC_TYPE, (byte)1);
        firework.setItemMeta(itemMeta);

        return firework;
    }

    private ItemStack createBaseFirework() {
        var fireworkConfig = plugin.getConfig().getConfigurationSection("firework");
        if (fireworkConfig == null) {
            return new ItemStack(Material.FIREWORK_ROCKET);
        }

        return XItemStack.deserialize(fireworkConfig);
    }

    public boolean isInfiniteFirework(ItemStack firework) {
        if (firework.getType() != Material.FIREWORK_ROCKET) {
            return false;
        }
        var itemMeta = checkNotNull(firework.getItemMeta(), "firework meta cannot be null");
        var pdc = itemMeta.getPersistentDataContainer();
        return pdc.has(infiniteFireworkKey, INFINITE_FIREWORK_PDC_TYPE);
    }

    public boolean isPlayerFireworkOnCooldown(Player player) {
        return fireworkCooldownCache.getIfPresent(player.getUniqueId()) != null;
    }

    public void setPlayerFireworkOnCooldown(Player player) {
        fireworkCooldownCache.put(player.getUniqueId(), Instant.now().plus(cooldown));
    }

    public boolean canUseCommands(CommandSender sender) {
        return sender.hasPermission("fireworkinfinitum.command");
    }

    public boolean hasFireworkCooldownBypass(Player player) {
        return player.hasPermission("fireworkinfinitum.bypass.cooldown");
    }

    public void notifyFireworkCooldown(Player player) {
        var message = plugin.getConfig().getString("firework-cooldown-message", "");
        if (message.isBlank()) return;

        var now = Instant.now();
        var cooldownExpiration = fireworkCooldownCache.getIfPresent(player.getUniqueId());
        if (cooldownExpiration == null) cooldownExpiration = now;

        var secondsLeft = max(1, cooldownExpiration.getEpochSecond() - now.getEpochSecond());
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message)
            .replace("%seconds%", String.valueOf(secondsLeft)));
    }
}
