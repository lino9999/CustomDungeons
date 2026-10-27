package com.lino.customdungeons.objective;

public class SurviveTimeObjective extends Objective {

    public SurviveTimeObjective(String displayName, int targetSeconds) {
        super(ObjectiveType.SURVIVE_TIME, displayName, targetSeconds);
    }

    @Override
    public Objective copy() {
        return new SurviveTimeObjective(displayName, targetAmount);
    }
}
