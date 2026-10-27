package com.lino.customdungeons.core;

import com.lino.customdungeons.CustomDungeons;
import com.lino.customdungeons.api.events.DungeonJoinEvent;
import com.lino.customdungeons.api.events.DungeonLeaveEvent;
import com.lino.customdungeons.config.Messages;
import com.lino.customdungeons.objective.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;

public class DungeonManager {
    private final CustomDungeons plugin;
    private final Map<String, Dungeon> dungeons = new HashMap<>();
    private final List<DungeonInstance> activeInstances = new ArrayList<>();
    private final Map<UUID, DungeonInstance> playerInstanceMap = new HashMap<>();

    public DungeonManager(CustomDungeons plugin) {
        this.plugin = plugin;
    }

    public void load() {
        dungeons.clear();
        File folder = plugin.getConfigManager().getDungeonsFolder();
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return;

        for (File file : files) {
            String id = file.getName().replace(".yml", "").toLowerCase();
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
            Dungeon dungeon = new Dungeon(id);

            dungeon.setEnabled(yaml.getBoolean("enabled", true));
            dungeon.setDisplayName(yaml.getString("display-name", id));
            dungeon.setDescription(yaml.getString("description", ""));
            dungeon.setMinPlayers(yaml.getInt("min-players", 1));
            dungeon.setMaxPlayers(yaml.getInt("max-players", 4));
            dungeon.setLives(yaml.getInt("lives", 3));
            dungeon.setTimeLimitSeconds(yaml.getInt("time-limit-seconds", 600));

            dungeon.setLobbyLocation(parseLocation(yaml.getString("locations.lobby")));
            dungeon.setSpawnLocation(parseLocation(yaml.getString("locations.spawn")));
            dungeon.setExitLocation(parseLocation(yaml.getString("locations.exit")));
            dungeon.setPos1(parseLocation(yaml.getString("locations.pos1")));
            dungeon.setPos2(parseLocation(yaml.getString("locations.pos2")));

            ConfigurationSection reqSec = yaml.getConfigurationSection("requirements");
            if (reqSec != null) {
                dungeon.setRequiredPermission(reqSec.getString("permission"));
                dungeon.setRequiredLevel(reqSec.getInt("min-level", 0));
                dungeon.setRequiredCost(reqSec.getDouble("cost", 0.0));
            }

            ConfigurationSection rewSec = yaml.getConfigurationSection("rewards");
            if (rewSec != null) {
                dungeon.setRewardCommands(rewSec.getStringList("commands"));
                dungeon.setRewardExperience(rewSec.getInt("experience", 0));
                dungeon.setRewardLootTable(rewSec.getString("loot-table"));
            }

            ConfigurationSection stagesSec = yaml.getConfigurationSection("stages");
            if (stagesSec != null) {
                for (String stageKey : stagesSec.getKeys(false)) {
                    ConfigurationSection sSec = stagesSec.getConfigurationSection(stageKey);
                    if (sSec == null) continue;

                    int num = Integer.parseInt(stageKey);
                    String name = sSec.getString("name", "Stage " + num);
                    int duration = sSec.getInt("duration-seconds", 180);
                    List<String> clearCmds = sSec.getStringList("clear-commands");

                    List<Objective> objs = new ArrayList<>();
                    List<Map<?, ?>> objMaps = sSec.getMapList("objectives");
                    for (Map<?, ?> oMap : objMaps) {
                        String typeStr = String.valueOf(oMap.get("type")).toUpperCase();
                        String disp = oMap.containsKey("display-name") ? String.valueOf(oMap.get("display-name")) : "Objective";
                        int amt = oMap.containsKey("amount") ? ((Number) oMap.get("amount")).intValue() : 1;

                        if ("KILL_MOBS".equals(typeStr)) {
                            EntityType et = oMap.containsKey("entity") ? EntityType.valueOf(String.valueOf(oMap.get("entity")).toUpperCase()) : null;
                            objs.add(new KillMobsObjective(disp, amt, et));
                        } else if ("INTERACT_BLOCK".equals(typeStr)) {
                            Material mat = oMap.containsKey("material") ? Material.matchMaterial(String.valueOf(oMap.get("material"))) : null;
                            objs.add(new InteractBlockObjective(disp, amt, mat));
                        } else if ("COLLECT_ITEMS".equals(typeStr)) {
                            Material mat = oMap.containsKey("material") ? Material.matchMaterial(String.valueOf(oMap.get("material"))) : null;
                            objs.add(new CollectItemsObjective(disp, amt, mat));
                        } else if ("SURVIVE_TIME".equals(typeStr)) {
                            objs.add(new SurviveTimeObjective(disp, amt));
                        }
                    }
                    dungeon.addStage(new DungeonStage(num, name, duration, objs, clearCmds));
                }
            }
            dungeons.put(id, dungeon);
        }
    }

    public DungeonInstance startInstance(Dungeon dungeon, List<Player> players) {
        DungeonInstance instance = new DungeonInstance(plugin, dungeon, players);
        activeInstances.add(instance);
        for (Player p : players) {
            playerInstanceMap.put(p.getUniqueId(), instance);
            Bukkit.getPluginManager().callEvent(new DungeonJoinEvent(p, instance));
        }
        instance.start();
        return instance;
    }

    public void removeInstance(DungeonInstance instance) {
        activeInstances.remove(instance);
        for (UUID uuid : instance.getParticipants()) {
            playerInstanceMap.remove(uuid);
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) {
                Bukkit.getPluginManager().callEvent(new DungeonLeaveEvent(p, instance));
            }
        }
    }

    public boolean canPlayerJoin(Player player, Dungeon dungeon) {
        if (!dungeon.isEnabled()) return false;
        if (dungeon.getRequiredPermission() != null && !player.hasPermission(dungeon.getRequiredPermission())) return false;
        if (dungeon.getRequiredLevel() > 0 && player.getLevel() < dungeon.getRequiredLevel()) return false;
        if (dungeon.getRequiredCost() > 0 && plugin.getVaultHook() != null && plugin.getVaultHook().hasEconomy()) {
            if (!plugin.getVaultHook().has(player, dungeon.getRequiredCost())) return false;
        }
        return true;
    }

    public void stopAllInstances() {
        List<DungeonInstance> copy = new ArrayList<>(activeInstances);
        for (DungeonInstance inst : copy) {
            inst.fail("Server shutting down.");
            inst.cleanup();
        }
    }

    public Optional<DungeonInstance> getInstance(Player player) {
        return Optional.ofNullable(playerInstanceMap.get(player.getUniqueId()));
    }

    public boolean isInDungeon(Player player) {
        return playerInstanceMap.containsKey(player.getUniqueId());
    }

    public Optional<Dungeon> getDungeon(String id) {
        return Optional.ofNullable(dungeons.get(id.toLowerCase()));
    }

    public Collection<Dungeon> getDungeons() {
        return Collections.unmodifiableCollection(dungeons.values());
    }

    public List<DungeonInstance> getActiveInstances() {
        return Collections.unmodifiableList(activeInstances);
    }

    private Location parseLocation(String str) {
        if (str == null || str.isEmpty()) return null;
        String[] parts = str.split(",");
        if (parts.length < 4) return null;
        World w = Bukkit.getWorld(parts[0].trim());
        if (w == null) return null;
        double x = Double.parseDouble(parts[1].trim());
        double y = Double.parseDouble(parts[2].trim());
        double z = Double.parseDouble(parts[3].trim());
        float yaw = parts.length > 4 ? Float.parseFloat(parts[4].trim()) : 0f;
        float pitch = parts.length > 5 ? Float.parseFloat(parts[5].trim()) : 0f;
        return new Location(w, x, y, z, yaw, pitch);
    }

    public String serializeLocation(Location loc) {
        if (loc == null || loc.getWorld() == null) return "";
        return loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ() + "," + loc.getYaw() + "," + loc.getPitch();
    }
}
