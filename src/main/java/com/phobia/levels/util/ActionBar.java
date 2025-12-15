package com.phobia.levels.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;

public class ActionBar {

    public static void send(Player player, String message) {
        if (player == null || !player.isOnline()) return;
        player.sendActionBar(Component.text(message));
    }

    public static void broadcast(String message) {
        Component component = Component.text(message);
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendActionBar(component);
        }
    }
}
