package com.phobia.levels.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.phobia.levels.LevelPlugin;
import com.phobia.levels.data.PlayerData;

public class GiveTokensCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // /givetokens <player> <amount>
        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /givetokens <player> <amount>");
            return true;
        }

        // Permission check
        if (!sender.hasPermission("levels.givetokens")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        // Target player check
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found or offline.");
            return true;
        }

        // Amount check
        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Amount must be a number.");
            return true;
        }

        if (amount <= 0) {
            sender.sendMessage(ChatColor.RED + "Amount must be greater than 0.");
            return true;
        }

        // Add tokens
        PlayerData data = LevelPlugin.getInstance().getPlayerDataManager().getData(target);
        data.addTokens(amount);

        // Save immediately
        LevelPlugin.getInstance().getPlayerDataManager().saveData(target);

        // Send feedback
        sender.sendMessage(ChatColor.GREEN + "Gave " + amount + " tokens to " + target.getName() + ".");
        target.sendMessage(ChatColor.GOLD + "You received " + amount + " tokens!");

        return true;
    }
}
