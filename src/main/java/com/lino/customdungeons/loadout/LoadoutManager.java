package com.lino.customdungeons.loadout;

import com.lino.customdungeons.CustomDungeons;
import com.lino.customdungeons.config.Messages;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;

public class LoadoutManager {
    private final CustomDungeons plugin;
    private final Map<String, Loadout> loadouts = new HashMap<>();

    public LoadoutManager(CustomDungeons plugin) {
        this.plugin = plugin;
    }

    public void load() {
        loadouts.clear();
        File folder = plugin.getConfigManager().getLoadoutsFolder();
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return;

        for (File file : files) {
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
            for (String key : yaml.getKeys(false)) {
                ConfigurationSection sec = yaml.getConfigurationSection(key);
                if (sec == null) continue;

                String displayName = Messages.colorize(sec.getString("display-name", key));
                String desc = Messages.colorize(sec.getString("description", ""));
                Material icon = Material.matchMaterial(sec.getString("icon", "IRON_SWORD"));
                if (icon == null) icon = Material.IRON_SWORD;

                Loadout loadout = new Loadout(key, displayName, desc, icon);

                ItemStack helmet = parseItem(sec.getString("helmet"));
                ItemStack chestplate = parseItem(sec.getString("chestplate"));
                ItemStack leggings = parseItem(sec.getString("leggings"));
                ItemStack boots = parseItem(sec.getString("boots"));
                loadout.setArmor(helmet, chestplate, leggings, boots);

                List<Map<?, ?>> itemsList = sec.getMapList("items");
                for (Map<?, ?> itemMap : itemsList) {
                    String matName = String.valueOf(itemMap.get("material"));
                    int amount = itemMap.containsKey("amount") ? ((Number) itemMap.get("amount")).intValue() : 1;
                    int slot = itemMap.containsKey("slot") ? ((Number) itemMap.get("slot")).intValue() : 0;
                    Material mat = Material.matchMaterial(matName);
                    if (mat != null) {
                        loadout.setInventoryItem(slot, new ItemStack(mat, amount));
                    }
                }
                loadouts.put(key.toLowerCase(), loadout);
            }
        }
    }

    private ItemStack parseItem(String materialName) {
        if (materialName == null || materialName.isEmpty()) return null;
        Material mat = Material.matchMaterial(materialName);
        return mat != null ? new ItemStack(mat) : null;
    }

    public Optional<Loadout> getLoadout(String id) {
        return Optional.ofNullable(loadouts.get(id.toLowerCase()));
    }

    public Collection<Loadout> getLoadouts() {
        return Collections.unmodifiableCollection(loadouts.values());
    }
}
