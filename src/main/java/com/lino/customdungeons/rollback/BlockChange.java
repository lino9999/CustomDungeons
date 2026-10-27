package com.lino.customdungeons.rollback;

import org.bukkit.Location;
import org.bukkit.block.data.BlockData;

public class BlockChange {
    private final Location location;
    private final BlockData originalData;

    public BlockChange(Location location, BlockData originalData) {
        this.location = location.clone();
        this.originalData = originalData.clone();
    }

    public Location getLocation() {
        return location;
    }

    public BlockData getOriginalData() {
        return originalData;
    }

    public void rollback() {
        location.getBlock().setBlockData(originalData, false);
    }
}
