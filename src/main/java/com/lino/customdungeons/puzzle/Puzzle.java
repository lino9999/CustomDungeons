package com.lino.customdungeons.puzzle;

import com.lino.customdungeons.core.DungeonInstance;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class Puzzle {
    protected final String id;
    protected final PuzzleType type;
    protected boolean solved = false;

    public Puzzle(String id, PuzzleType type) {
        this.id = id;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public PuzzleType getType() {
        return type;
    }

    public boolean isSolved() {
        return solved;
    }

    public abstract void onInteract(Player player, Location clickedLocation, DungeonInstance instance);

    public abstract void onReset(DungeonInstance instance);

    public abstract Puzzle copy();
}
