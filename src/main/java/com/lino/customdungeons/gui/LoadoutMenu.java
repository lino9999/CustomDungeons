package com.lino.customdungeons.gui;

import com.lino.customdungeons.CustomDungeons;
import com.lino.customdungeons.config.Messages;
import com.lino.customdungeons.loadout.Loadout;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LoadoutMenu extends Menu {
    private final CustomDungeons plugin;

    public LoadoutMenu(CustomDungeons plugin, Player player) {
        super(player, "&8&lSelect Class Loadout", 27);
        this.plugin = plugin;
    }

    @Override
    public void build() {
        Collection<Loadout> list = plugin.getLoadoutManager().getLoadouts();
        int slot = 10;
        for (Loadout loadout : list) {
            ItemStack icon = new ItemStack(loadout.getIcon() != null ? loadout.getIcon() : Material.CHEST);
            ItemMeta meta = icon.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(loadout.getDisplayName());
                List<String> lore = new ArrayList<>();
                lore.add(loadout.getDescription());
                lore.add("");
                lore.add(Messages.colorize("&aClick to equip class."));
                meta.setLore(lore);
                icon.setItemMeta(meta);
            }
            setItem(slot++, new MenuItem(icon, event -> {
                player.closeInventory();
                loadout.apply(player);
                Messages.send(player, "loadout.selected", java.util.Map.of("loadout", loadout.getDisplayName()));
            }));
            if (slot > 16) break;
        }
    }
}
