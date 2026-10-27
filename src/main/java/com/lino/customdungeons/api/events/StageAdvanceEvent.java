package com.lino.customdungeons.api.events;

import com.lino.customdungeons.core.DungeonInstance;
import com.lino.customdungeons.core.DungeonStage;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class StageAdvanceEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final DungeonInstance instance;
    private final DungeonStage stage;
    private final int stageNumber;

    public StageAdvanceEvent(DungeonInstance instance, DungeonStage stage, int stageNumber) {
        this.instance = instance;
        this.stage = stage;
        this.stageNumber = stageNumber;
    }

    public DungeonInstance getInstance() {
        return instance;
    }

    public DungeonStage getStage() {
        return stage;
    }

    public int getStageNumber() {
        return stageNumber;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
