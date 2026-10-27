package com.lino.customdungeons;

import com.lino.customdungeons.api.CustomDungeonsAPI;
import com.lino.customdungeons.commands.CommandDispatcher;
import com.lino.customdungeons.config.ConfigManager;
import com.lino.customdungeons.core.DungeonManager;
import com.lino.customdungeons.gui.MenuListener;
import com.lino.customdungeons.hooks.PlaceholderAPIHook;
import com.lino.customdungeons.hooks.VaultHook;
import com.lino.customdungeons.listeners.DungeonListener;
import com.lino.customdungeons.listeners.PlayerLifecycleListener;
import com.lino.customdungeons.loadout.LoadoutManager;
import com.lino.customdungeons.loot.LootManager;
import com.lino.customdungeons.party.PartyManager;
import com.lino.customdungeons.queue.QueueManager;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class CustomDungeons extends JavaPlugin {
    private static CustomDungeons instance;
    private CustomDungeonsAPI api;
    private ConfigManager configManager;
    private LootManager lootManager;
    private LoadoutManager loadoutManager;
    private DungeonManager dungeonManager;
    private PartyManager partyManager;
    private QueueManager queueManager;
    private VaultHook vaultHook;

    @Override
    public void onEnable() {
        instance = this;

        this.configManager = new ConfigManager(this);
        this.lootManager = new LootManager(this);
        this.loadoutManager = new LoadoutManager(this);
        this.dungeonManager = new DungeonManager(this);
        this.partyManager = new PartyManager(this);
        this.queueManager = new QueueManager(this);
        this.api = new CustomDungeonsAPI(this);

        this.lootManager.load();
        this.loadoutManager.load();
        this.dungeonManager.load();

        this.vaultHook = new VaultHook();
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderAPIHook(this).register();
        }

        Bukkit.getPluginManager().registerEvents(new MenuListener(), this);
        Bukkit.getPluginManager().registerEvents(new DungeonListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerLifecycleListener(this), this);

        PluginCommand cmd = getCommand("customdungeons");
        if (cmd != null) {
            CommandDispatcher dispatcher = new CommandDispatcher(this);
            cmd.setExecutor(dispatcher);
            cmd.setTabCompleter(dispatcher);
        }

        getLogger().info("CustomDungeons v" + getDescription().getVersion() + " by Lino enabled successfully!");
    }

    @Override
    public void onDisable() {
        if (dungeonManager != null) {
            dungeonManager.stopAllInstances();
        }
        getLogger().info("CustomDungeons disabled.");
    }

    public static CustomDungeons getInstance() {
        return instance;
    }

    public CustomDungeonsAPI getAPI() {
        return api;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public LootManager getLootManager() {
        return lootManager;
    }

    public LoadoutManager getLoadoutManager() {
        return loadoutManager;
    }

    public DungeonManager getDungeonManager() {
        return dungeonManager;
    }

    public PartyManager getPartyManager() {
        return partyManager;
    }

    public QueueManager getQueueManager() {
        return queueManager;
    }

    public VaultHook getVaultHook() {
        return vaultHook;
    }
}
