package com.phobia.levels.managers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.phobia.levels.LevelPlugin;
import com.phobia.levels.data.PlayerData;

public class PlayerDataManager {

    private final Map<Player, PlayerData> dataMap = new HashMap<>();

    public PlayerData getData(Player player) {
        if (dataMap.containsKey(player)) {
            return dataMap.get(player);
        }

        PlayerData data = new PlayerData(player);
        load(player, data);
        dataMap.put(player, data);

        return data;
    }

    private void load(Player player, PlayerData data) {
        File file = getPlayerFile(player);

        if (!file.exists()) {
            save(player);
            return;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        data.load(config);
    }

    public void saveAll() {
        for (Player player : dataMap.keySet()) {
            save(player);
        }
    }

    public void save(Player player) {
        PlayerData data = dataMap.get(player);
        if (data == null) return;

        File file = getPlayerFile(player);
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        data.save(config);

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveData(Player player) {
        save(player);
    }

    public void saveAndRemove(Player player) {
        save(player);
        dataMap.remove(player);
    }

    public void unload(Player player) {
        save(player);
        dataMap.remove(player);
    }

    public File getPlayerFile(Player player) {
        return new File(LevelPlugin.getInstance().getDataFolder(),
                "playerdata/" + player.getUniqueId() + ".yml");
    }

    // -------- OFFLINE PLAYER SUPPORT ----------------

    public File getOfflinePlayerFile(OfflinePlayer offline) {
        return new File(LevelPlugin.getInstance().getDataFolder(),
                "playerdata/" + offline.getUniqueId() + ".yml");
    }

    public PlayerData loadOfflineData(OfflinePlayer offline) {
        File file = getOfflinePlayerFile(offline);
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        // Create temporary data object (no Player instance)
        PlayerData data = new PlayerData(null);
        data.load(config);

        return data;
    }
}
