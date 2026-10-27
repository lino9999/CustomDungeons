package com.lino.customdungeons.gui;

import com.lino.customdungeons.CustomDungeons;
import com.lino.customdungeons.config.Messages;
import com.lino.customdungeons.core.Dungeon;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DungeonListMenu extends Menu {
    private final CustomDungeons plugin;

    public DungeonListMenu(CustomDungeons plugin, Player player) {
        super(player, "&8&lAvailable Dungeons", 54);
        this.plugin = plugin;
    }

    @Override
    public void build() {
        Collection<Dungeon> list = plugin.getDungeonManager().getDungeons();
        int slot = 10;

        for (Dungeon dungeon : list) {
            ItemStack icon = new ItemStack(dungeon.isEnabled() ? Material.IRON_DOOR : Material.BARRIER);
            ItemMeta meta = icon.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(dungeon.getDisplayName());
                List<String> lore = new ArrayList<>();
                lore.add(Messages.colorize("&7" + dungeon.getDescription()));
                lore.add("");
                lore.add(Messages.colorize("&ePlayers: &6" + dungeon.getMinPlayers() + " - " + dungeon.getMaxPlayers()));
                lore.add(Messages.colorize("&eStages: &6" + dungeon.getStages().size()));
                lore.add(Messages.colorize("&eTime Limit: &6" + (dungeon.getTimeLimitSeconds() / 60) + "m"));
                lore.add(Messages.colorize("&eLives: &6" + dungeon.getLives()));
                lore.add("");
                lore.add(Messages.colorize(dungeon.isEnabled() ? "&aClick to join or queue!" : "&cCurrently Disabled"));
                meta.setLore(lore);
                icon.setItemMeta(meta);
            }

            setItem(slot, new MenuItem(icon, event -> {
                player.closeInventory();
                if (!dungeon.isEnabled()) {
                    player.sendMessage(Messages.colorize("&cThis dungeon is disabled."));
                    return;
                }
                plugin.getQueueManager().enqueue(player, dungeon.getId());
            }));

            slot++;
            if ((slot + 1) % 9 == 0) {
                slot += 2;
            }
            if (slot >= 44) break;
        }

        ItemStack close = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = close.getItemMeta();
        if (closeMeta != null) {
            closeMeta.setDisplayName(Messages.colorize("&cClose"));
            close.setItemMeta(closeMeta);
        }
        setItem(49, new MenuItem(close, event -> player.closeInventory()));
    }
}
