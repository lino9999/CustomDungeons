package com.lino.customdungeons.loadout;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;

public class Loadout {
    private final String id;
    private final String displayName;
    private final String description;
    private final Material icon;
    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private final Map<Integer, ItemStack> inventoryItems = new HashMap<>();

    public Loadout(String id, String displayName, String description, Material icon) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.icon = icon;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public Material getIcon() {
        return icon;
    }

    public void setArmor(ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots) {
        this.helmet = helmet;
        this.chestplate = chestplate;
        this.leggings = leggings;
        this.boots = boots;
    }

    public void setInventoryItem(int slot, ItemStack item) {
        this.inventoryItems.put(slot, item);
    }

    public void apply(Player player) {
        PlayerInventory inv = player.getInventory();
        inv.clear();
        if (helmet != null) inv.setHelmet(helmet.clone());
        if (chestplate != null) inv.setChestplate(chestplate.clone());
        if (leggings != null) inv.setLeggings(leggings.clone());
        if (boots != null) inv.setBoots(boots.clone());

        for (Map.Entry<Integer, ItemStack> entry : inventoryItems.entrySet()) {
            if (entry.getKey() >= 0 && entry.getKey() < 36) {
                inv.setItem(entry.getKey(), entry.getValue().clone());
            } else if (entry.getKey() == 40) {
                inv.setItemInOffHand(entry.getValue().clone());
            }
        }
    }
}
