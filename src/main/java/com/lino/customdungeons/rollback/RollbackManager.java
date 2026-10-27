package com.lino.customdungeons.rollback;

import org.bukkit.Location;
import org.bukkit.block.data.BlockData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RollbackManager {
    private final Map<Location, BlockChange> changes = new HashMap<>();

    public void recordChange(Location loc, BlockData originalData) {
        if (!changes.containsKey(loc)) {
            changes.put(loc.clone(), new BlockChange(loc, originalData));
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
