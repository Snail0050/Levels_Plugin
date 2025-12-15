package com.phobia.levels.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.phobia.levels.LevelPlugin;

public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        LevelPlugin.getInstance().getPlayerDataManager().saveAndRemove(player);

        if (LevelPlugin.getInstance().getScoreboardHandler() != null) {
            LevelPlugin.getInstance().getScoreboardHandler().removeScoreboard(player);
        }
    }
}
