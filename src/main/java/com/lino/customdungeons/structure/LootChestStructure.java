package com.lino.customdungeons.structure;

import com.lino.customdungeons.config.Messages;
import com.lino.customdungeons.core.DungeonInstance;
import com.lino.customdungeons.loot.LootTable;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class LootChestStructure extends Structure {
    private final String lootTableId;
    private final boolean mimic;
    private final Set<UUID> openedPlayers = new HashSet<>();

    public LootChestStructure(String id, Location location, String lootTableId, boolean mimic) {
        super(id, StructureType.LOOT_CHEST, location);
        this.lootTableId = lootTableId;
        this.mimic = mimic;
    }

    @Override
    public void onInteract(Player player, DungeonInstance instance) {
        if (location == null || location.getWorld() == null) return;
        if (openedPlayers.contains(player.getUniqueId())) {
            player.sendMessage(Messages.colorize("&7You have already looted this chest."));
            return;
        }
        openedPlayers.add(player.getUniqueId());

        if (mimic) {
            player.playSound(location, Sound.ENTITY_CREEPER_PRIMED, 1.0f, 0.5f);
            player.damage(6.0);
            player.sendMessage(Messages.colorize("&c&lIt was a Mimic! The chest bit you!"));
            return;
        }

        instance.getPlugin().getLootManager().getTable(lootTableId).ifPresent(table -> {
            List<ItemStack> items = table.generateLoot();
            if (location.getBlock().getState() instanceof Chest chest) {
                for (ItemStack item : items) {
                    chest.getInventory().addItem(item);
                }
            } else {
                for (ItemStack item : items) {
                    location.getWorld().dropItemNaturally(location, item);
                }
            }
            player.playSound(location, Sound.BLOCK_CHEST_OPEN, 1.0f, 1.0f);
            player.sendMessage(Messages.colorize("&aYou opened the dungeon chest!"));
        });
    }

    @Override
    public void onReset(DungeonInstance instance) {
        openedPlayers.clear();
        if (location != null && location.getWorld() != null && location.getBlock().getState() instanceof Chest chest) {
            chest.getInventory().clear();
        }
    }

    @Override
    public Structure copy() {
        return new LootChestStructure(id, location, lootTableId, mimic);
    }
}
