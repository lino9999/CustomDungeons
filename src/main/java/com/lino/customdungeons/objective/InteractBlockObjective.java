package com.lino.customdungeons.objective;

import org.bukkit.Material;

public class InteractBlockObjective extends Objective {
    private final Material targetMaterial;

    public InteractBlockObjective(String displayName, int targetAmount, Material targetMaterial) {
        super(ObjectiveType.INTERACT_BLOCK, displayName, targetAmount);
        this.targetMaterial = targetMaterial;
    }

    public Material getTargetMaterial() {
        return targetMaterial;
    }

    public boolean matches(Material material) {
        return targetMaterial == null || targetMaterial == material;
    }

    @Override
    public Objective copy() {
        return new InteractBlockObjective(displayName, targetAmount, targetMaterial);
    }
}
