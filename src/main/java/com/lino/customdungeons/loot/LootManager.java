package com.lino.customdungeons.loot;

import com.lino.customdungeons.CustomDungeons;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;

public class LootManager {
    private final CustomDungeons plugin;
    private final Map<String, LootTable> tables = new HashMap<>();
    private final Map<UUID, List<ItemStack>> pendingRewards = new HashMap<>();

    public LootManager(CustomDungeons plugin) {
        this.plugin = plugin;
    }

    public void load() {
        tables.clear();
        File folder = plugin.getConfigManager().getLootFolder();
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return;

        for (File file : files) {
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
            for (String key : yaml.getKeys(false)) {
                ConfigurationSection sec = yaml.getConfigurationSection(key);
                if (sec == null) continue;

                int rolls = sec.getInt("rolls", 1);
                List<LootItem> items = new ArrayList<>();
                List<Map<?, ?>> rawItems = sec.getMapList("items");
                for (Map<?, ?> map : rawItems) {
                    Material mat = Material.matchMaterial(String.valueOf(map.get("material")));
                    if (mat == null) continue;

                    String name = map.containsKey("name") ? String.valueOf(map.get("name")) : null;
                    List<String> lore = map.containsKey("lore") && map.get("lore") instanceof List<?> ? (List<String>) map.get("lore") : null;
                    double chance = map.containsKey("chance") ? ((Number) map.get("chance")).doubleValue() : 100.0;
                    int minAmount = map.containsKey("min-amount") ? ((Number) map.get("min-amount")).intValue() : 1;
                    int maxAmount = map.containsKey("max-amount") ? ((Number) map.get("max-amount")).intValue() : 1;

                    Map<Enchantment, Integer> enchants = new HashMap<>();
                    if (map.containsKey("enchantments") && map.get("enchantments") instanceof Map<?, ?> encMap) {
                        for (Map.Entry<?, ?> e : encMap.entrySet()) {
                            String encName = String.valueOf(e.getKey()).toLowerCase();
                            Enchantment ench = Registry.ENCHANTMENT.get(NamespacedKey.minecraft(encName));
                            if (ench != null && e.getValue() instanceof Number lvl) {
                                enchants.put(ench, lvl.intValue());
                            }
                        }
                    }

                    items.add(new LootItem(mat, name, lore, enchants, chance, minAmount, maxAmount));
                }
                tables.put(key.toLowerCase(), new LootTable(key, rolls, items));
            }
        }
    }

    public Optional<LootTable> getTable(String id) {
        return Optional.ofNullable(tables.get(id.toLowerCase()));
    }

    public void addPendingRewards(Player player, List<ItemStack> items) {
        if (items == null || items.isEmpty()) return;
        pendingRewards.computeIfAbsent(player.getUniqueId(), k -> new ArrayList<>()).addAll(items);
    }

    public List<ItemStack> getPendingRewards(Player player) {
        return pendingRewards.getOrDefault(player.getUniqueId(), Collections.emptyList());
    }

    public List<ItemStack> claimRewards(Player player) {
        List<ItemStack> claimed = pendingRewards.remove(player.getUniqueId());
        return claimed != null ? claimed : Collections.emptyList();
    }
}
