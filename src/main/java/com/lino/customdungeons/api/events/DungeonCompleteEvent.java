package com.lino.customdungeons.api.events;

import com.lino.customdungeons.core.DungeonInstance;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DungeonCompleteEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final DungeonInstance instance;

    public DungeonCompleteEvent(DungeonInstance instance) {
        this.instance = instance;
    }

    public DungeonInstance getInstance() {
        return instance;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
