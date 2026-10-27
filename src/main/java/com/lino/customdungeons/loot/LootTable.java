package com.lino.customdungeons.loot;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class LootTable {
    private final String id;
    private final int rolls;
    private final List<LootItem> items;

    public LootTable(String id, int rolls, List<LootItem> items) {
        this.id = id;
        this.rolls = rolls;
        this.items = items;
    }

    public String getId() {
        return id;
    }

    public List<ItemStack> generateLoot() {
        List<ItemStack> result = new ArrayList<>();
        for (int r = 0; r < Math.max(1, rolls); r++) {
            for (LootItem item : items) {
                if (item.roll()) {
                    result.add(item.create());
                }
            }
        }
        return result;
    }
}
