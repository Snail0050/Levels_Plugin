package com.phobia.levels.scoreboard;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.phobia.levels.LevelPlugin;

/**
 * ScoreboardHandler manages PlayerBoard instances and updates them periodically.
 * Uses UUID keys so boards persist correctly across player object changes.
 */
public class ScoreboardHandler {

    private final Map<UUID, PlayerBoard> boards = new HashMap<>();
    private boolean running = false;

    /**
     * Start periodic updates (call once on plugin enable).
     */
    public void start() {
        if (running) return;
        running = true;

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    giveScoreboard(player); // ensures board exists
                    PlayerBoard board = boards.get(player.getUniqueId());
                    if (board != null) board.update();
                }
            }
        }.runTaskTimer(LevelPlugin.getInstance(), 20L, 20L);
    }

    /**
     * Give a scoreboard to a player (creates PlayerBoard if needed).
     */
    public void giveScoreboard(Player player) {
        UUID id = player.getUniqueId();
        boards.computeIfAbsent(id, k -> new PlayerBoard(player));
    }

    /**
     * Remove a player's scoreboard (call on quit).
     */
    public void removeScoreboard(Player player) {
        PlayerBoard board = boards.remove(player.getUniqueId());
        if (board != null) {
            board.destroy(); // reset to main scoreboard
        }
    }

    /**
     * Remove all scoreboards and cleanup (call on disable).
     */
    public void shutdown() {
        for (PlayerBoard pb : boards.values()) {
            try {
                pb.destroy();
            } catch (Exception ignored) {}
        }
        boards.clear();
        running = false;
    }

    /**
     * Accessor used by other code if needed.
     */
    public PlayerBoard getBoard(Player player) {
        return boards.get(player.getUniqueId());
    }
}
