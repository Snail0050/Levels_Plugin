package com.phobia.levels.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import com.phobia.levels.LevelPlugin;
import com.phobia.levels.data.PlayerData;

public class PlayerBoard {

    private final Player player;
    private Scoreboard scoreboard;
    private Objective objective;

    public PlayerBoard(Player player) {
        this.player = player;
        create();
    }

    public void create() {
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        objective = scoreboard.registerNewObjective("levels", "dummy",
                ChatColor.YELLOW.toString() + ChatColor.BOLD + "Stats");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        update();
        player.setScoreboard(scoreboard);
    }

    public void update() {
    if (objective == null) return;

    PlayerData data = LevelPlugin.getInstance()
            .getPlayerDataManager()
            .getData(player);

    double playerMult = LevelPlugin.getInstance().getPlayerMultiplier(player);
    double globalMult = LevelPlugin.getInstance().getGlobalBooster();
    long boosterTime = LevelPlugin.getInstance().getBoosterTimeRemaining();

    // clear old entries
    scoreboard.getEntries().forEach(scoreboard::resetScores);

    int line = 15;

    // Level
    addLine(ChatColor.WHITE + "Level: " + ChatColor.GREEN + data.getLevel(), line--);

    // XP / Required XP
    addLine(ChatColor.WHITE + "XP: " + ChatColor.GREEN + data.getXp()
            + ChatColor.GRAY + "/" + ChatColor.GREEN + data.getRequiredXp(), line--);

    // Kills
    addLine(ChatColor.WHITE + "Kills: " + ChatColor.GREEN + data.getKills(), line--);

    // Deaths
    addLine(ChatColor.WHITE + "Deaths: " + ChatColor.RED + data.getDeaths(), line--);

    // KDR
    String kdrFormatted = String.format("%.2f", data.getKdr());
    addLine(ChatColor.WHITE + "KDR: " + ChatColor.GOLD + kdrFormatted, line--);

    // Tokens
    addLine(ChatColor.WHITE + "Tokens: " + ChatColor.YELLOW + data.getTokens(), line--);

    // ============================
    // NEW: Online Players
    // ============================
    addLine(ChatColor.WHITE + "Online: " + ChatColor.GREEN + Bukkit.getOnlinePlayers().size(), line--);

    // Blank separator
    addLine("", line--);

    // ============================
    // Boost Lines
    // ============================
    addLine(ChatColor.WHITE + "Your Boost: " + ChatColor.AQUA + "x" + playerMult, line--);

    if (globalMult > 1.0 || boosterTime > 0) {
        addLine(ChatColor.WHITE + "Server Boost: " + ChatColor.LIGHT_PURPLE + "x" + globalMult, line--);

        if (boosterTime > 0) {
            long minutes = boosterTime / 60;
            long seconds = boosterTime % 60;
            addLine(ChatColor.GRAY + "Ends in: " + minutes + "m " + seconds + "s", line--);
        }
    }

    addLine(ChatColor.DARK_GRAY + "mcguns.net", 0);
}


    private void addLine(String text, int score) {
        Score s = objective.getScore(text);
        s.setScore(score);
    }

    public void destroy() {
        try {
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        } catch (Exception ignored) {}
        scoreboard = null;
        objective = null;
    }
}
