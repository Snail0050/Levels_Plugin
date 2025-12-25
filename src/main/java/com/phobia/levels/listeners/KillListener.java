package com.phobia.levels.listeners;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.metadata.MetadataValue;

import com.phobia.levels.LevelPlugin;
import com.phobia.levels.data.PlayerData;
import com.phobia.levels.scoreboard.PlayerBoard;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class KillListener implements Listener {

    private final Map<Integer, Set<UUID>> mobContributors = new HashMap<>();
    private final Map<Integer, UUID> lastKiller = new HashMap<>();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity) || event.getEntity() instanceof Player) return;

        Player damager = getDamager(event.getDamager());
        if (damager == null) return;

        int entityId = event.getEntity().getEntityId();
        mobContributors.computeIfAbsent(entityId, k -> new HashSet<>()).add(damager.getUniqueId());
        lastKiller.put(entityId, damager.getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMobDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player) return;

        LivingEntity victim = event.getEntity();
        int entityId = victim.getEntityId();

        if (!mobContributors.containsKey(entityId)) return;

        Set<UUID> contributors = mobContributors.remove(entityId);
        UUID killerUUID = lastKiller.remove(entityId);

        if (contributors == null) return;

        Player killer = (killerUUID != null) ? Bukkit.getPlayer(killerUUID) : null;
        if (victim.getKiller() != null) killer = victim.getKiller();

        // --- RETRIEVE MOB LEVEL FROM METADATA ---
        int mobLevel = 1;
        if (victim.hasMetadata("mob_level")) {
            for (MetadataValue value : victim.getMetadata("mob_level")) {
                mobLevel = value.asInt();
                break; 
            }
        }

        // --- CONSOLIDATED REWARD MATH ---
        // Killer gets exactly 1 XP per Level.
        int finalXp = mobLevel; 
        // Killer gets 1 token per 10 levels (Tier system).
        int finalTokens = Math.max(1, (int) Math.ceil(mobLevel / 10.0));

        // 1. Reward Killer
        if (killer != null && killer.isOnline()) {
            reward(killer, finalTokens, finalXp, false);
            contributors.remove(killer.getUniqueId());
        }

        // 2. Reward Assistants (1/3 of the killer's reward)
        int assistTokens = Math.max(1, finalTokens / 3);
        int assistXp = Math.max(1, finalXp / 3);

        for (UUID uuid : contributors) {
            Player assistant = Bukkit.getPlayer(uuid);
            if (assistant != null && assistant.isOnline()) {
                reward(assistant, assistTokens, assistXp, true);
            }
        }
    }

    private void reward(Player player, int tokens, int baseXp, boolean isAssist) {
        PlayerData data = LevelPlugin.getInstance().getPlayerDataManager().getData(player);
        
        if (!isAssist) data.addMobKill();
        if (tokens > 0) data.addTokens(tokens);

        // Boosters apply to the final XP payout
        double multi = LevelPlugin.getInstance().getPlayerMultiplier(player);
        double global = LevelPlugin.getInstance().getGlobalBooster();
        int finalXp = (int) Math.round(baseXp * multi * global);

        data.addXp(finalXp);
        updateBoard(player);
        LevelPlugin.getInstance().getPlayerDataManager().save(player);

        String prefix = isAssist ? ChatColor.GRAY + "[Assist] " : "";
        player.spigot().sendMessage(
            ChatMessageType.ACTION_BAR,
            new TextComponent(prefix + ChatColor.GREEN + "+" + finalXp + " XP "
            + ChatColor.GRAY + " |" + ChatColor.YELLOW + " +" + tokens + " Tokens")
        );
    }

    private Player getDamager(org.bukkit.entity.Entity entity) {
        if (entity instanceof Player) return (Player) entity;
        if (entity instanceof Projectile) {
            Projectile p = (Projectile) entity;
            if (p.getShooter() instanceof Player) return (Player) p.getShooter();
        }
        return null;
    }

    @EventHandler
    public void onPlayerKill(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        PlayerData victimData = LevelPlugin.getInstance().getPlayerDataManager().getData(victim);
        victimData.addDeath();
        updateBoard(victim);
        LevelPlugin.getInstance().getPlayerDataManager().save(victim);

        if (killer == null) return;

        // Player kills give fixed rewards
        reward(killer, 5, 25, false);
    }

    private void updateBoard(Player p) {
        PlayerBoard board = LevelPlugin.getInstance().getScoreboardHandler().getBoard(p);
        if (board != null) board.update();
    }
}