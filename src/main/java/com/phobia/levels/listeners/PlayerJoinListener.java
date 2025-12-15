package com.phobia.levels.listeners;

import com.phobia.levels.LevelPlugin;
import com.phobia.levels.data.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Loads player data into memory and gives the scoreboard when a player joins.
 */
public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Ensure PlayerData is loaded and cached
        PlayerData data = LevelPlugin.getInstance().getPlayerDataManager().getData(player);
        // (optional) you can do something with data here

        // Give the player a scoreboard immediately
        if (LevelPlugin.getInstance().getScoreboardHandler() != null) {
            LevelPlugin.getInstance().getScoreboardHandler().giveScoreboard(player);
        }
    }
}
