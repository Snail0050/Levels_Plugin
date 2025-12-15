package com.phobia.levels.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.phobia.levels.LevelPlugin;
import com.phobia.levels.managers.LevelManager;

public class GiveXpCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /givexp <player> <amount>");
            return true;
        }

        if (!sender.hasPermission("levels.givexp")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission.");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return true;
        }

        int baseAmount;
        try {
            baseAmount = Integer.parseInt(args[1]);
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Amount must be a number.");
            return true;
        }

        if (baseAmount <= 0) {
            sender.sendMessage(ChatColor.RED + "Amount must be > 0.");
            return true;
        }

        LevelManager manager = LevelPlugin.getInstance().getLevelManager();
        manager.addXp(target, baseAmount);

        sender.sendMessage(ChatColor.GREEN + "Gave " + baseAmount + " XP to " + target.getName());
        target.sendMessage(ChatColor.AQUA + "You received " + baseAmount + " XP!");

        LevelPlugin.getInstance().getPlayerDataManager().save(target);
        return true;
    }
}
