package com.lino.customdungeons.structure;

import com.lino.customdungeons.core.DungeonInstance;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class Structure {
    protected final String id;
    protected final StructureType type;
    protected final Location location;

    public Structure(String id, StructureType type, Location location) {
        this.id = id;
        this.type = type;
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public StructureType getType() {
        return type;
    }

    public Location getLocation() {
        return location;
    }

    public abstract void onInteract(Player player, DungeonInstance instance);

    public abstract void onReset(DungeonInstance instance);

    public abstract Structure copy();
}
