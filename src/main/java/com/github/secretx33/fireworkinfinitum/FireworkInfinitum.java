package com.github.secretx33.fireworkinfinitum;

import com.github.secretx33.fireworkinfinitum.commands.GiveCommand;
import com.github.secretx33.fireworkinfinitum.commands.ReloadCommand;
import com.github.secretx33.fireworkinfinitum.eventlistener.FireworkListener;
import com.github.secretx33.fireworkinfinitum.manager.FireworkManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;

public class FireworkInfinitum extends JavaPlugin {

    @Override
    public void onEnable() {
        var start = System.nanoTime();
        saveDefaultConfig();

        var fireworkManager = new FireworkManager(this);
        getServer().getPluginManager().registerEvents(new FireworkListener(fireworkManager), this);
        new GiveCommand(this, fireworkManager);
        new ReloadCommand(this, fireworkManager);

        var startedMsg = "[%s] Loaded (in %d ms)".formatted(FireworkInfinitum.class.getSimpleName(), Duration.ofNanos(System.nanoTime() - start).toMillis());
        getServer().getConsoleSender().sendMessage(startedMsg);
    }
}
