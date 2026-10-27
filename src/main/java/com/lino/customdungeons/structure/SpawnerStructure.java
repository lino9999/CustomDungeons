package com.lino.customdungeons.structure;

import com.lino.customdungeons.core.DungeonInstance;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SpawnerStructure extends Structure {
    private final EntityType entityType;
    private final int maxEntities;
    private final int intervalSeconds;
    private int counter = 0;
    private final List<LivingEntity> spawned = new ArrayList<>();

    public SpawnerStructure(String id, Location location, EntityType entityType, int maxEntities, int intervalSeconds) {
        super(id, StructureType.SPAWNER, location);
        this.entityType = entityType;
        this.maxEntities = maxEntities;
        this.intervalSeconds = intervalSeconds;
    }

    public void tick(DungeonInstance instance) {
        spawned.removeIf(e -> e.isDead() || !e.isValid());
        counter++;
        if (counter >= intervalSeconds) {
            counter = 0;
            if (spawned.size() < maxEntities && location.getWorld() != null) {
                Entity entity = location.getWorld().spawnEntity(location, entityType);
                if (entity instanceof LivingEntity living) {
                    spawned.add(living);
                    instance.trackEntity(living);
                }
            }
        }
    }

    @Override
    public void onInteract(Player player, DungeonInstance instance) {
    }

    @Override
    public void onReset(DungeonInstance instance) {
        for (LivingEntity e : spawned) {
            if (e.isValid()) {
                e.remove();
            }
        }
        spawned.clear();
        counter = 0;
    }
}
