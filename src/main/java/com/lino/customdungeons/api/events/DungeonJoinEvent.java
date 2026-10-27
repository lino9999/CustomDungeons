package com.lino.customdungeons.api.events;

import com.lino.customdungeons.core.DungeonInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DungeonJoinEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final DungeonInstance instance;

    public DungeonJoinEvent(Player player, DungeonInstance instance) {
        this.player = player;
        this.instance = instance;
    }

    public Player getPlayer() {
        return player;
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
