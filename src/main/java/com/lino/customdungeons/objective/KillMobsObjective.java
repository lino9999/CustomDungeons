package com.lino.customdungeons.objective;

import org.bukkit.entity.EntityType;

public class KillMobsObjective extends Objective {
    private final EntityType entityType;

    public KillMobsObjective(String displayName, int targetAmount, EntityType entityType) {
        super(ObjectiveType.KILL_MOBS, displayName, targetAmount);
        this.entityType = entityType;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public boolean matches(EntityType type) {
        return entityType == null || entityType == type;
    }

    @Override
    public Objective copy() {
        return new KillMobsObjective(displayName, targetAmount, entityType);
    }
}
