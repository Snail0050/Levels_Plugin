package com.phobia.levels;

import org.bukkit.entity.Player;

import com.phobia.levels.data.PlayerData;

public class LevelsAPI {

    private final LevelPlugin plugin;

    public LevelsAPI(LevelPlugin plugin) {
        this.plugin = plugin;
    }

    public int getLevel(Player p) {
        PlayerData data = plugin.getPlayerDataManager().getData(p);
        return (data != null) ? data.getLevel() : 1;
    }
}
