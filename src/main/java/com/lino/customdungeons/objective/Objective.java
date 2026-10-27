package com.lino.customdungeons.objective;

import com.lino.customdungeons.config.Messages;

public abstract class Objective {
    protected final ObjectiveType type;
    protected final String displayName;
    protected final int targetAmount;
    protected int currentAmount = 0;

    public Objective(ObjectiveType type, String displayName, int targetAmount) {
        this.type = type;
        this.displayName = Messages.colorize(displayName);
        this.targetAmount = targetAmount;
    }

    public ObjectiveType getType() {
        return type;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getTargetAmount() {
        return targetAmount;
    }

    public int getCurrentAmount() {
        return currentAmount;
    }

    public void increment(int amount) {
        this.currentAmount = Math.min(targetAmount, currentAmount + amount);
    }

    public boolean isCompleted() {
        return currentAmount >= targetAmount;
    }

    public String getProgressBar() {
        return displayName + " &8[&e" + currentAmount + "&8/&6" + targetAmount + "&8]";
    }

    public abstract Objective copy();
}
