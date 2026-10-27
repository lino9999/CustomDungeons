package com.lino.customdungeons.gui;

import com.lino.customdungeons.config.Messages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.HashMap;
import java.util.Map;

public abstract class Menu implements InventoryHolder {
    protected final Player player;
    protected final String title;
    protected final int size;
    protected final Map<Integer, MenuItem> items = new HashMap<>();
    protected Inventory inventory;

    public Menu(Player player, String title, int size) {
        this.player = player;
        this.title = Messages.colorize(title);
        this.size = size;
    }

    public abstract void build();

    public void setItem(int slot, MenuItem item) {
        items.put(slot, item);
        if (inventory != null && item != null) {
            inventory.setItem(slot, item.getItemStack());
        }
    }

    public void open() {
        this.inventory = Bukkit.createInventory(this, size, title);
        items.clear();
        build();
        for (Map.Entry<Integer, MenuItem> entry : items.entrySet()) {
            if (entry.getKey() >= 0 && entry.getKey() < size && entry.getValue() != null) {
                inventory.setItem(entry.getKey(), entry.getValue().getItemStack());
            }
        }
        player.openInventory(inventory);
    }

    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        MenuItem item = items.get(event.getRawSlot());
        if (item != null) {
            item.execute(event);
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public Player getPlayer() {
        return player;
    }
}
