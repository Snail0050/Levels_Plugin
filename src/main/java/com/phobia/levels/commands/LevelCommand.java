package com.phobia.levels.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.phobia.levels.LevelPlugin;
import com.phobia.levels.data.PlayerData;

public class LevelCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        PlayerData data = LevelPlugin.getInstance().getPlayerDataManager().getData(player);

        player.sendMessage(ChatColor.WHITE + "=== " + ChatColor.GRAY + "Level Info" + ChatColor.WHITE + " ===");
        player.sendMessage(ChatColor.GRAY + "Level: " + ChatColor.YELLOW + data.getLevel());
        player.sendMessage(ChatColor.GRAY + "XP: " + ChatColor.YELLOW + data.getXp());
        player.sendMessage(ChatColor.GRAY + "Next Level: " + ChatColor.YELLOW + data.getRequiredXp());

        return true;
    }
}
