package com.lino.customdungeons.rollback;

import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RollbackManager {
    private final Map<Location, BlockChange> changes = new HashMap<>();

    public void recordChange(Block block) {
        Location loc = block.getLocation();
        if (!changes.containsKey(loc)) {
            changes.put(loc, new BlockChange(loc, block.getBlockData()));
        }
    }

    public void performRollback() {
        List<BlockChange> list = new ArrayList<>(changes.values());
        for (BlockChange change : list) {
            change.rollback();
        }
        changes.clear();
    }

    public void clear() {
        changes.clear();
    }
}
