package com.phobia.levels.commands;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.phobia.levels.LevelPlugin;
import com.phobia.levels.data.PlayerData;
import com.phobia.levels.managers.PlayerDataManager;

public class ProfileCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        PlayerDataManager manager = LevelPlugin.getInstance().getPlayerDataManager();

        // ---- /profile ----
        if (args.length == 0) {

            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Console must use /profile <player>");
                return true;
            }

            Player player = (Player) sender;
            PlayerData data = manager.getData(player);

            sendProfile(player, player.getName(), data);
            return true;
        }

        // ---- /profile <player> ----
        String targetName = args[0];

        // Try online first
        Player online = Bukkit.getPlayerExact(targetName);
        if (online != null) {
            PlayerData data = manager.getData(online);
            //sender.sendMessage(ChatColor.WHITE + "=== " + ChatColor.GRAY + "Profile:" + ChatColor.GOLD + online.getName() + ChatColor.WHITE + " ===");
            sendProfile(sender, online.getName(), data);
            return true;
        }

        // Try offline
        OfflinePlayer offline = Bukkit.getOfflinePlayer(targetName);

        if (!offline.hasPlayedBefore()) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return true;
        }

        // Load offline player's data from disk (creates temporary object)
        File playerFile = manager.getOfflinePlayerFile(offline);
        if (!playerFile.exists()) {
            sender.sendMessage(ChatColor.RED + "No saved data for this player.");
            return true;
        }

        PlayerData offlineData = manager.loadOfflineData(offline);

        sendProfile(sender, offline.getName(), offlineData);

        return true;
    }

    private void sendProfile(CommandSender viewer, String name, PlayerData data) {
        viewer.sendMessage(ChatColor.WHITE + "=== " + ChatColor.GRAY + "Profile:" + ChatColor.GOLD + " " + name + ChatColor.WHITE + " ===");
        viewer.sendMessage(ChatColor.GRAY + "Level: " + ChatColor.YELLOW + data.getLevel());
        viewer.sendMessage(ChatColor.GRAY + "XP: " + ChatColor.YELLOW + data.getXp());
        viewer.sendMessage(ChatColor.GRAY + "Next Level: " + ChatColor.YELLOW + data.getRequiredXp() + "xp");
        viewer.sendMessage(ChatColor.GRAY + "Kills: " + ChatColor.YELLOW + data.getKills());
        viewer.sendMessage(ChatColor.GRAY + "Deaths: " + ChatColor.YELLOW + data.getDeaths());
        viewer.sendMessage(ChatColor.GRAY + "KDR: " + ChatColor.YELLOW + String.format("%.2f", data.getKdr()));
        viewer.sendMessage(ChatColor.GRAY + "Tokens: " + ChatColor.YELLOW + data.getTokens());
    }
}
