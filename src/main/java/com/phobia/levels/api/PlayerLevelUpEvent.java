package com.phobia.levels.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.phobia.levels.LevelPlugin;
import com.phobia.levels.data.PlayerData;

public class PlayerLevelUpEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final int newLevel;

    public PlayerLevelUpEvent(Player player, int newLevel) {
        this.player = player;
        this.newLevel = newLevel;
    }

    /**
     * Helper method to fetch a player's level from the DataManager.
     * Can be called via PlayerLevelUpEvent.getLevel(player);
     */
    public static int getLevel(Player player) {
        if (player == null) return 1;
        PlayerData data = LevelPlugin.getInstance().getPlayerDataManager().getData(player);
        return (data != null) ? data.getLevel() : 1;
    }

    public Player getPlayer() { 
        return player; 
    }
    
    public int getNewLevel() { 
        return newLevel; 
    }

    @Override
    public HandlerList getHandlers() { 
        return HANDLERS; 
    }
    
    public static HandlerList getHandlerList() { 
        return HANDLERS; 
    }
}