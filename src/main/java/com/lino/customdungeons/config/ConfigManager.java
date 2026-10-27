package com.lino.customdungeons.config;

import com.lino.customdungeons.CustomDungeons;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class ConfigManager {
    private final CustomDungeons plugin;
    private FileConfiguration messagesConfig;
    private File dungeonsFolder;
    private File lootFolder;
    private File loadoutsFolder;

    public ConfigManager(CustomDungeons plugin) {
        this.plugin = plugin;
        this.init();
    }

    public void init() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();

        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            copyResource("messages.yml", messagesFile);
        }
        this.messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
        Messages.init(messagesConfig);

        this.dungeonsFolder = new File(plugin.getDataFolder(), "dungeons");
        if (!dungeonsFolder.exists()) {
            dungeonsFolder.mkdirs();
            File sample = new File(dungeonsFolder, "catacombs.yml");
            copyResource("dungeons/catacombs.yml", sample);
        }

        this.lootFolder = new File(plugin.getDataFolder(), "loot");
        if (!lootFolder.exists()) {
            lootFolder.mkdirs();
            File sampleLoot = new File(lootFolder, "default_loot.yml");
            copyResource("loot/default_loot.yml", sampleLoot);
        }

        this.loadoutsFolder = new File(plugin.getDataFolder(), "loadouts");
        if (!loadoutsFolder.exists()) {
            loadoutsFolder.mkdirs();
            File sampleLoadout = new File(loadoutsFolder, "classes.yml");
            copyResource("loadouts/classes.yml", sampleLoadout);
        }
    }

    private void copyResource(String resourcePath, File targetFile) {
        try (InputStream in = plugin.getResource(resourcePath)) {
            if (in != null) {
                targetFile.getParentFile().mkdirs();
                Files.copy(in, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to copy default resource " + resourcePath + ": " + e.getMessage());
        }
    }

    public void reload() {
        init();
        plugin.getLootManager().load();
        plugin.getLoadoutManager().load();
        plugin.getDungeonManager().load();
    }

    public File getDungeonsFolder() {
        return dungeonsFolder;
    }

    public File getLootFolder() {
        return lootFolder;
    }

    public File getLoadoutsFolder() {
        return loadoutsFolder;
    }
}
