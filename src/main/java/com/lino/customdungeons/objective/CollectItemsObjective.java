package com.lino.customdungeons.objective;

import org.bukkit.Material;

public class CollectItemsObjective extends Objective {
    private final Material material;

    public CollectItemsObjective(String displayName, int targetAmount, Material material) {
        super(ObjectiveType.COLLECT_ITEMS, displayName, targetAmount);
        this.material = material;
    }

    public Material getMaterial() {
        return material;
    }

    public boolean matches(Material mat) {
        return material == null || material == mat;
    }

    @Override
    public Objective copy() {
        return new CollectItemsObjective(displayName, targetAmount, material);
    }
}
