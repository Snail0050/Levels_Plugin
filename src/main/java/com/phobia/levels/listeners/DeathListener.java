package com.phobia.levels.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.phobia.levels.LevelPlugin;
import com.phobia.levels.data.PlayerData;

public class DeathListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        PlayerData data = LevelPlugin.getInstance()
                .getPlayerDataManager()
                .getData(event.getEntity());

        data.addDeath();
        LevelPlugin.getInstance().getPlayerDataManager().save(event.getEntity());
    }
}
