package com.lino.customdungeons.api.events;

import com.lino.customdungeons.core.DungeonInstance;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DungeonFailEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final DungeonInstance instance;
    private final String reason;

    public DungeonFailEvent(DungeonInstance instance, String reason) {
        this.instance = instance;
        this.reason = reason;
    }

    public DungeonInstance getInstance() {
        return instance;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
