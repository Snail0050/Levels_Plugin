package com.phobia.levels.managers;

import org.bukkit.entity.Player;

import com.phobia.levels.LevelPlugin;
import com.phobia.levels.data.PlayerData;

public class LevelManager {

    /**
     * XP is already multiplied in KillListener.
     * This method now simply applies the raw amount.
     */
    public void addXp(Player player, int amount) {
        PlayerData data = LevelPlugin.getInstance().getPlayerDataManager().getData(player);
        data.addXp(amount);
    }

    public int getLevel(Player player) {
        return LevelPlugin.getInstance().getPlayerDataManager().getData(player).getLevel();
    }

    public int getXp(Player player) {
        return LevelPlugin.getInstance().getPlayerDataManager().getData(player).getXp();
    }

    public int getRequiredXp(Player player) {
        return LevelPlugin.getInstance().getPlayerDataManager().getData(player).getRequiredXp();
    }
}
