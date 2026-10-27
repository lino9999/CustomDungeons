package com.lino.customdungeons.listeners;

import com.lino.customdungeons.CustomDungeons;
import com.lino.customdungeons.core.DungeonInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Optional;

public class PlayerLifecycleListener implements Listener {
    private final CustomDungeons plugin;

    public PlayerLifecycleListener(CustomDungeons plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        Optional<DungeonInstance> instOpt = plugin.getDungeonManager().getInstance(player);
        instOpt.ifPresent(instance -> instance.handlePlayerDeath(player));

        plugin.getQueueManager().dequeue(player);
        plugin.getPartyManager().removeMember(player);
    }
}
