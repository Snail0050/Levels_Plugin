package com.phobia.levels.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.phobia.levels.LevelPlugin;

public class XpBoostCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // -----------------------------------------
        //  /xpboost → anyone with view permission
        // -----------------------------------------
        if (args.length == 0) {

            if (!sender.hasPermission("levels.xpboost.view") &&
                !sender.hasPermission("levels.xpboost.manage")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission.");
                return true;
            }

            double global = LevelPlugin.getInstance().getGlobalBooster();
            long remaining = LevelPlugin.getInstance().getBoosterTimeRemaining();

            if (global <= 1.0) {
                sender.sendMessage(ChatColor.YELLOW + "There is no active XP boost.");
                return true;
            }

            long mins = remaining / 60;
            long secs = remaining % 60;

            sender.sendMessage(ChatColor.LIGHT_PURPLE + "Global XP Boost Active:");
            sender.sendMessage(ChatColor.GRAY + "  Multiplier: " + ChatColor.AQUA + "x" + global);
            sender.sendMessage(ChatColor.GRAY + "  Time Left: " + ChatColor.AQUA + mins + "m " + secs + "s");
            return true;
        }

        // -----------------------------------------
        //  The remaining commands require manage
        // -----------------------------------------
        if (!sender.hasPermission("levels.xpboost.manage")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission.");
            return true;
        }

        // /xpboost stop ----------------------------
        if (args.length == 1 && args[0].equalsIgnoreCase("stop")) {

            if (LevelPlugin.getInstance().getGlobalBooster() <= 1.0) {
                sender.sendMessage(ChatColor.YELLOW + "There is no active XP boost to stop.");
                return true;
            }

            LevelPlugin.getInstance().setGlobalBooster(1.0, 0);

            Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD +
                    "GLOBAL XP BOOST HAS BEEN STOPPED!");
            return true;
        }

        // /xpboost <multiplier> <seconds> ----------
        if (args.length != 2) {
            sender.sendMessage(ChatColor.YELLOW + "Usage: /xpboost <multiplier> <seconds>");
            sender.sendMessage(ChatColor.YELLOW + "       /xpboost stop");
            return true;
        }

        double multiplier;
        int seconds;

        try {
            multiplier = Double.parseDouble(args[0]);
            seconds = Integer.parseInt(args[1]);
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Invalid number.");
            return true;
        }

        if (multiplier < 1.0) {
            sender.sendMessage(ChatColor.RED + "Multiplier must be at least 1.0.");
            return true;
        }
        if (seconds <= 0) {
            sender.sendMessage(ChatColor.RED + "Seconds must be > 0.");
            return true;
        }

        LevelPlugin.getInstance().setGlobalBooster(multiplier, seconds);

        long mins = seconds / 60;
        long secs = seconds % 60;

        Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD +
                "GLOBAL XP BOOST ACTIVATED!");
        Bukkit.broadcastMessage(ChatColor.AQUA + "Multiplier: " + ChatColor.WHITE + multiplier + "x");
        Bukkit.broadcastMessage(ChatColor.AQUA + "Duration: " + ChatColor.WHITE + mins + "m " + secs + "s");
        return true;
    }
}
