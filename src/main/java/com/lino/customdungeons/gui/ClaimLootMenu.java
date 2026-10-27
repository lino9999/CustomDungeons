package com.lino.customdungeons.gui;

import com.lino.customdungeons.CustomDungeons;
import com.lino.customdungeons.config.Messages;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ClaimLootMenu extends Menu {
    private final CustomDungeons plugin;

    public ClaimLootMenu(CustomDungeons plugin, Player player) {
        super(player, "&8&lUnclaimed Dungeon Rewards", 54);
        this.plugin = plugin;
    }

    @Override
    public void build() {
        List<ItemStack> rewards = plugin.getLootManager().getPendingRewards(player);
        if (rewards.isEmpty()) {
            ItemStack empty = new ItemStack(Material.STRUCTURE_VOID);
            ItemMeta meta = empty.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(Messages.colorize("&7No pending rewards"));
                empty.setItemMeta(meta);
            }
            setItem(22, new MenuItem(empty));
            return;
        }

        int slot = 0;
        for (ItemStack item : rewards) {
            setItem(slot++, new MenuItem(item.clone()));
            if (slot >= 45) break;
        }

        ItemStack claimAll = new ItemStack(Material.CHEST);
        ItemMeta meta = claimAll.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(Messages.colorize("&a&lClaim All Rewards"));
            claimAll.setItemMeta(meta);
        }
        setItem(49, new MenuItem(claimAll, event -> {
            player.closeInventory();
            List<ItemStack> claimed = plugin.getLootManager().claimRewards(player);
            for (ItemStack item : claimed) {
                java.util.Map<Integer, ItemStack> leftover = player.getInventory().addItem(item);
                for (ItemStack drop : leftover.values()) {
                    player.getWorld().dropItemNaturally(player.getLocation(), drop);
                }
            }
            Messages.send(player, "loot.claimed");
        }));
    }
}
