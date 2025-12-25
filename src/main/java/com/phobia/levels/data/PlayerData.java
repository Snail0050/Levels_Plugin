package com.phobia.levels.data;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.phobia.levels.api.PlayerLevelUpEvent;

public class PlayerData {

    private final Player player;
    private int level;
    private int xp;
    private int requiredXp;
    private int kills;
    private int mobKills; // Added Mob Kills
    private int deaths;
    private int tokens;

    public PlayerData(Player player) {
        this.player = player;
        this.level = 1;
        this.xp = 0;
        this.requiredXp = 100;
        this.kills = 0;
        this.mobKills = 0;
        this.deaths = 0;
        this.tokens = 0;
    }

    public Player getPlayer() { return player; }

    public void load(FileConfiguration config) {
        this.level = config.getInt("level", 1);
        this.xp = config.getInt("xp", 0);
        this.requiredXp = config.getInt("requiredXp", 100);
        this.kills = config.getInt("kills", 0);
        this.mobKills = config.getInt("mobKills", 0); // Load new stat
        this.deaths = config.getInt("deaths", 0);
        this.tokens = config.getInt("tokens", 0);
    }

    public void save(FileConfiguration config) {
        config.set("level", level);
        config.set("xp", xp);
        config.set("requiredXp", requiredXp);
        config.set("kills", kills);
        config.set("mobKills", mobKills); // Save new stat
        config.set("deaths", deaths);
        config.set("tokens", tokens);
    }

    public int getLevel() { return level; }
    public int getXp() { return xp; }
    public int getRequiredXp() { return requiredXp; }
    public int getKills() { return kills; }
    public int getMobKills() { return mobKills; } // Getter
    public int getDeaths() { return deaths; }
    public int getTokens() { return tokens; }

    public void setLevel(int level) { this.level = level; }
    public void setXp(int xp) { this.xp = xp; }
    public void setRequiredXp(int requiredXp) { this.requiredXp = requiredXp; }
    public void setKills(int kills) { this.kills = kills; }
    public void setMobKills(int mobKills) { this.mobKills = mobKills; } // Setter
    public void setDeaths(int deaths) { this.deaths = deaths; }
    public void setTokens(int tokens) { this.tokens = tokens; }

    public void addXp(int amount) {
        this.xp += amount;
        checkLevelUp();
    }

    public void addKill() { this.kills++; }
    public void addMobKill() { this.mobKills++; } // Adder
    public void addDeath() { this.deaths++; }

    public void addTokens(int amount) {
        this.tokens += amount;
    }

    public boolean removeTokens(int amount) {
        if (tokens < amount) return false;
        tokens -= amount;
        return true;
    }

    // Player Kills / Deaths
    public double getKdr() {
        return deaths == 0 ? kills : (double) kills / deaths;
    }

    // (Player Kills + Mob Kills) / Deaths
    public double getTkdr() {
        int totalKills = kills + mobKills;
        return deaths == 0 ? totalKills : (double) totalKills / deaths;
    }

    private void checkLevelUp() {
        while (xp >= requiredXp) {
            xp -= requiredXp;
            level++;
            requiredXp = (int) (requiredXp * 1.25);

            Bukkit.getPluginManager().callEvent(new PlayerLevelUpEvent(player, level));

            this.tokens += 25;
            player.sendMessage("§a§lLEVEL UP! §7You are now level §b§l" + level + "§7!");
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
            player.sendMessage("§eYou received §625 tokens§e!");
        }
    }
}