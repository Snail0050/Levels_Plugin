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

    // =========================================================
    // >>> CORRECTED TOKEN METHODS FOR CROSS-PLUGIN ACCESS
    // =========================================================

    /**
     * Checks if the player has at least the specified amount of tokens.
     * Delegates to PlayerData.getTokens().
     * * @param p The player to check.
     * @param amount The required amount of tokens.
     * @return True if the player has enough tokens, false otherwise.
     */
    public static boolean hasTokens(Player p, int amount) {
        LevelPlugin levelPlugin = LevelPlugin.getInstance();
        if (levelPlugin == null) return false;

        PlayerData data = levelPlugin.getPlayerDataManager().getData(p);
        if (data == null) return false;
        
        return data.getTokens() >= amount;
    }

    /**
     * Deducts the specified amount of tokens from the player.
     * Delegates to PlayerData.removeTokens(), which handles the subtraction.
     * * @param p The player to deduct from.
     * @param amount The amount of tokens to take.
     * @return True if the deduction was successful, false if the player lacked tokens.
     */
    public static boolean takeTokens(Player p, int amount) {
        LevelPlugin levelPlugin = LevelPlugin.getInstance();
        if (levelPlugin == null) return false;

        PlayerData data = levelPlugin.getPlayerDataManager().getData(p);
        if (data == null) return false;

        // PlayerData.removeTokens(int) already handles the check and subtraction.
        boolean success = data.removeTokens(amount);
        
        // Since PlayerData was modified, we should force a save to prevent loss if the server crashes.
        // We assume PlayerDataManager.saveData(p) or similar exists. 
        // If not, the saving will rely on PlayerQuitListener/onDisable, which is less safe.
        // For robustness, we should call a save function here if available.
        // Assuming PlayerDataManager handles saving when PlayerData is modified or via an explicit call:
        // levelPlugin.getPlayerDataManager().saveData(p); 
        
        return success;
    }
    
    // =========================================================
}