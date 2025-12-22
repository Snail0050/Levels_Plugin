package com.phobia.levels.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

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

        setupTeams();

        update();
        player.setScoreboard(scoreboard);
    }

    private void setupTeams() {
        createLine("level", ChatColor.BLACK.toString(), 15);
        createLine("xp", ChatColor.DARK_BLUE.toString(), 14);
        createLine("kills", ChatColor.DARK_GREEN.toString(), 13);
        createLine("deaths", ChatColor.DARK_AQUA.toString(), 12);
        createLine("kdr", ChatColor.DARK_RED.toString(), 11);
        createLine("tokens", ChatColor.DARK_PURPLE.toString(), 10);
        createLine("online", ChatColor.GOLD.toString(), 9);
        
        objective.getScore(ChatColor.RESET.toString()).setScore(8);

        createLine("pboost", ChatColor.GRAY.toString(), 7);
        createLine("sboost", ChatColor.BLUE.toString(), 6);
        createLine("timer", ChatColor.GREEN.toString(), 5);

        // Footer - Centered with spaces
        objective.getScore(ChatColor.DARK_GRAY + "  mcguns.net  ").setScore(0);
    }

    private void createLine(String teamId, String entry, int score) {
        Team team = scoreboard.registerNewTeam(teamId);
        team.addEntry(entry);
        objective.getScore(entry).setScore(score);
    }

    public void update() {
        if (objective == null) return;

        PlayerData data = LevelPlugin.getInstance().getPlayerDataManager().getData(player);
        double playerMult = LevelPlugin.getInstance().getPlayerMultiplier(player);
        double globalMult = LevelPlugin.getInstance().getGlobalBooster();
        long boosterTime = LevelPlugin.getInstance().getBoosterTimeRemaining();

        // Update Stats
        updateTeamText("level", ChatColor.WHITE + "Level: " + ChatColor.GREEN + data.getLevel());
        updateTeamText("xp", ChatColor.WHITE + "XP: " + ChatColor.GREEN + data.getXp() + ChatColor.GRAY + "/" + ChatColor.GREEN + data.getRequiredXp());
        updateTeamText("kills", ChatColor.WHITE + "Kills: " + ChatColor.GREEN + data.getKills());
        updateTeamText("deaths", ChatColor.WHITE + "Deaths: " + ChatColor.RED + data.getDeaths());
        
        String kdrFormatted = String.format("%.2f", data.getKdr());
        updateTeamText("kdr", ChatColor.WHITE + "KDR: " + ChatColor.GOLD + kdrFormatted);
        
        updateTeamText("tokens", ChatColor.WHITE + "Tokens: " + ChatColor.YELLOW + data.getTokens());
        updateTeamText("online", ChatColor.WHITE + "Online: " + ChatColor.GREEN + Bukkit.getOnlinePlayers().size());
        
        updateTeamText("pboost", ChatColor.WHITE + "Your Boost: " + ChatColor.AQUA + "x" + playerMult);

        // Booster Display Logic
        if (globalMult > 1.0) {
            updateTeamText("sboost", ChatColor.WHITE + "Server Boost: " + ChatColor.LIGHT_PURPLE + "x" + globalMult);
            
            if (boosterTime > 0) {
                long minutes = boosterTime / 60;
                long seconds = boosterTime % 60;
                updateTeamText("timer", ChatColor.GRAY + "Ends in: " + ChatColor.WHITE + minutes + "m " + seconds + "s");
            } else {
                updateTeamText("timer", ""); 
            }
        } else {
            // Split the "No active booster" message across two lines
            updateTeamText("sboost", ChatColor.GRAY + "" + ChatColor.ITALIC + "*No active");
            updateTeamText("timer", ChatColor.GRAY + "" + ChatColor.ITALIC + " booster*");
        }
    }

    private void updateTeamText(String teamId, String text) {
        Team team = scoreboard.getTeam(teamId);
        if (team != null) {
            team.setPrefix(text);
        }
    }

    public void destroy() {
        try {
            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        } catch (Exception ignored) {}
        scoreboard = null;
        objective = null;
    }
}