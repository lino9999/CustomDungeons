package com.lino.customdungeons.structure;

import com.lino.customdungeons.core.DungeonInstance;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

import java.util.List;

public class DoorStructure extends Structure {
    private final List<Location> blocks;
    private final Material openMaterial;
    private final Material closedMaterial;
    private boolean open = false;

    public DoorStructure(String id, Location triggerLocation, List<Location> blocks, Material openMaterial, Material closedMaterial) {
        super(id, StructureType.DOOR, triggerLocation);
        this.blocks = blocks;
        this.openMaterial = openMaterial != null ? openMaterial : Material.AIR;
        this.closedMaterial = closedMaterial != null ? closedMaterial : Material.IRON_BARS;
    }

    public void setOpen(boolean open) {
        this.open = open;
        Material mat = open ? openMaterial : closedMaterial;
        BlockData data = mat.createBlockData();
        for (Location loc : blocks) {
            loc.getBlock().setBlockData(data, false);
        }
        if (location != null && location.getWorld() != null) {
            location.getWorld().playSound(location, open ? Sound.BLOCK_IRON_DOOR_OPEN : Sound.BLOCK_IRON_DOOR_CLOSE, 1.0f, 1.0f);
        }
    }

    @Override
    public void onInteract(Player player, DungeonInstance instance) {
        setOpen(!open);
    }

    @Override
    public void onReset(DungeonInstance instance) {
        setOpen(false);
    }

    @Override
    public Structure copy() {
        return new DoorStructure(id, location, new java.util.ArrayList<>(blocks), openMaterial, closedMaterial);
    }
}
