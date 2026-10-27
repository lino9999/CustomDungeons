package com.lino.customdungeons.loot;

import com.lino.customdungeons.config.Messages;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class LootItem {
    private final Material material;
    private final String name;
    private final List<String> lore;
    private final Map<Enchantment, Integer> enchantments;
    private final double chance;
    private final int minAmount;
    private final int maxAmount;

    public LootItem(Material material, String name, List<String> lore, Map<Enchantment, Integer> enchantments, double chance, int minAmount, int maxAmount) {
        this.material = material;
        this.name = name;
        this.lore = lore;
        this.enchantments = enchantments;
        this.chance = chance;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
    }

    public boolean roll() {
        return ThreadLocalRandom.current().nextDouble(0.0, 100.0) <= chance;
    }

    public ItemStack create() {
        int amount = minAmount >= maxAmount ? minAmount : ThreadLocalRandom.current().nextInt(minAmount, maxAmount + 1);
        ItemStack stack = new ItemStack(material, Math.max(1, amount));
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            if (name != null && !name.isEmpty()) {
                meta.setDisplayName(Messages.colorize(name));
            }
            if (lore != null && !lore.isEmpty()) {
                List<String> colored = new ArrayList<>();
                for (String line : lore) {
                    colored.add(Messages.colorize(line));
                }
                meta.setLore(colored);
            }
            stack.setItemMeta(meta);
        }
        if (enchantments != null) {
            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                stack.addUnsafeEnchantment(entry.getKey(), entry.getValue());
            }
        }
        return stack;
    }
}
