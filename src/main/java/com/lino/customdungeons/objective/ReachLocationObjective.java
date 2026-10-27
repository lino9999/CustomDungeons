package com.lino.customdungeons.objective;

import org.bukkit.Location;

public class ReachLocationObjective extends Objective {
    private final Location targetLocation;
    private final double radius;

    public ReachLocationObjective(String displayName, Location targetLocation, double radius) {
        super(ObjectiveType.REACH_LOCATION, displayName, 1);
        this.targetLocation = targetLocation;
        this.radius = radius;
    }

    public Location getTargetLocation() {
        return targetLocation;
    }

    public boolean isWithin(Location loc) {
        if (loc == null || targetLocation == null) return false;
        if (loc.getWorld() == null || targetLocation.getWorld() == null) return false;
        if (!loc.getWorld().equals(targetLocation.getWorld())) return false;
        return loc.distanceSquared(targetLocation) <= (radius * radius);
    }

    @Override
    public Objective copy() {
        return new ReachLocationObjective(displayName, targetLocation, radius);
    }
}
