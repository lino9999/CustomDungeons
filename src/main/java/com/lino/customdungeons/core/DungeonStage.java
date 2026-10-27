package com.lino.customdungeons.core;

import com.lino.customdungeons.config.Messages;
import com.lino.customdungeons.objective.Objective;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DungeonStage {
    private final int stageNumber;
    private final String name;
    private final int durationSeconds;
    private final List<Objective> objectives;
    private final List<String> clearCommands;

    public DungeonStage(int stageNumber, String name, int durationSeconds, List<Objective> objectives, List<String> clearCommands) {
        this.stageNumber = stageNumber;
        this.name = Messages.colorize(name);
        this.durationSeconds = durationSeconds;
        this.objectives = objectives != null ? objectives : new ArrayList<>();
        this.clearCommands = clearCommands != null ? clearCommands : Collections.emptyList();
    }

    public int getStageNumber() {
        return stageNumber;
    }

    public String getName() {
        return name;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

    public List<Objective> getObjectives() {
        return Collections.unmodifiableList(objectives);
    }

    public List<String> getClearCommands() {
        return Collections.unmodifiableList(clearCommands);
    }

    public List<Objective> createFreshObjectives() {
        List<Objective> copies = new ArrayList<>();
        for (Objective obj : objectives) {
            copies.add(obj.copy());
        }
        return copies;
    }
}
