package com.phobia.levels.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.phobia.levels.LevelPlugin;
import com.phobia.levels.data.PlayerData;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class KillListener implements Listener {

    // ======================================================
    // PLAYER KILLS (Fixes the issue — always fires reliably)
    // ======================================================
    @EventHandler
    public void onPlayerKill(PlayerDeathEvent event) {

        Player victim = event.getEntity();
        Player killer = victim.getKiller();
        if (killer == null) return;  // No killer → no XP

        FileConfiguration cfg = LevelPlugin.getInstance().getConfig();

        PlayerData killerData = LevelPlugin.getInstance()
            .getPlayerDataManager()
            .getData(killer);

        // record killer's kill
        killerData.addKill();

        // record victim’s death (DeathListener also does this — but harmless)
        PlayerData victimData = LevelPlugin.getInstance()
            .getPlayerDataManager()
            .getData(victim);
        victimData.addDeath();
        LevelPlugin.getInstance().getPlayerDataManager().save(victim);

        // If LeveledMobs is present → exit (LM handles XP)
        if (Bukkit.getPluginManager().getPlugin("LeveledMobs") != null) {
            LevelPlugin.getInstance().getPlayerDataManager().save(killer);
            return;
        }

        // Reward values
        int tokensAwarded = cfg.getInt("tokens.player-kill", 5);
        int baseXp = cfg.getInt("xp.player-kill", 25);

        if (tokensAwarded > 0) killerData.addTokens(tokensAwarded);

        double multi = LevelPlugin.getInstance().getPlayerMultiplier(killer);
        double global = LevelPlugin.getInstance().getGlobalBooster();

        int finalXp = (int) Math.round(baseXp * multi * global);

        int oldLevel = killerData.getLevel();
        killerData.addXp(finalXp);

        // Action bar
        killer.spigot().sendMessage(
            ChatMessageType.ACTION_BAR,
            new TextComponent(ChatColor.GREEN + "+" + finalXp + " XP "
            + ChatColor.AQUA + " | +" + tokensAwarded + " Tokens")
        );

        // Level up message
        if (killerData.getLevel() > oldLevel) {
            killer.sendMessage(ChatColor.GOLD + "⚡ LEVEL UP! You are now level "
                    + ChatColor.YELLOW + killerData.getLevel() + ChatColor.GOLD + "!");
        }

        LevelPlugin.getInstance().getPlayerDataManager().save(killer);
    }



    // ======================================================
    // MOB KILLS (still handled on EntityDeathEvent)
    // ======================================================
    @EventHandler
    public void onMobKill(EntityDeathEvent event) {

        // Ignore player deaths here — handled above
        if (event.getEntity() instanceof Player) return;

        Player killer = event.getEntity().getKiller();
        if (killer == null) return;


        FileConfiguration cfg = LevelPlugin.getInstance().getConfig();
        PlayerData data = LevelPlugin.getInstance().getPlayerDataManager().getData(killer);

        // If LeveledMobs present → don't process fallback XP
        if (Bukkit.getPluginManager().getPlugin("LeveledMobs") != null) {
            LevelPlugin.getInstance().getPlayerDataManager().save(killer);
            return;
        }

        EntityType type = event.getEntityType();
        String key = type.name().toLowerCase();

        int tokensAwarded = cfg.getInt("tokens.mob-kill." + key,
                cfg.getInt("tokens.mob-kill.default", 1));

        int baseXp = cfg.getInt("xp.mob-kill." + key,
                cfg.getInt("xp.mob-kill.default", 5));

        if (tokensAwarded > 0) data.addTokens(tokensAwarded);

        double multi = LevelPlugin.getInstance().getPlayerMultiplier(killer);
        double global = LevelPlugin.getInstance().getGlobalBooster();

        int finalXp = (int) Math.round(baseXp * multi * global);

        int oldLevel = data.getLevel();
        data.addXp(finalXp);

        killer.spigot().sendMessage(
            ChatMessageType.ACTION_BAR,
            new TextComponent(ChatColor.GREEN + "+" + finalXp + " XP "
            + ChatColor.AQUA + " | +" + tokensAwarded + " Tokens")
        );

        if (data.getLevel() > oldLevel) {
            killer.sendMessage(ChatColor.GOLD + "⚡ LEVEL UP! You are now level "
                    + ChatColor.YELLOW + data.getLevel() + ChatColor.GOLD + "!");
        }

        LevelPlugin.getInstance().getPlayerDataManager().save(killer);
    }
}
