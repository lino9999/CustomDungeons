package com.lino.customdungeons.puzzle;

import com.lino.customdungeons.config.Messages;
import com.lino.customdungeons.core.DungeonInstance;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DialCodePuzzle extends Puzzle {
    private final List<Location> dialLocations;
    private final List<Integer> targetCombination;
    private final Map<Location, Integer> currentValues = new HashMap<>();

    public DialCodePuzzle(String id, List<Location> dialLocations, List<Integer> targetCombination) {
        super(id, PuzzleType.DIAL_CODE);
        this.dialLocations = dialLocations;
        this.targetCombination = targetCombination;
        for (Location loc : dialLocations) {
            currentValues.put(loc, 0);
        }
    }

    @Override
    public void onInteract(Player player, Location clickedLocation, DungeonInstance instance) {
        if (solved) return;
        if (!currentValues.containsKey(clickedLocation)) return;

        int current = (currentValues.get(clickedLocation) + 1) % 10;
        currentValues.put(clickedLocation, current);
        player.playSound(clickedLocation, Sound.BLOCK_LEVER_CLICK, 1.0f, 1.0f + (current * 0.1f));
        player.sendMessage(Messages.colorize("&7Dial set to: &e" + current));

        boolean match = true;
        for (int i = 0; i < dialLocations.size(); i++) {
            Location loc = dialLocations.get(i);
            int expected = targetCombination.get(i);
            if (currentValues.getOrDefault(loc, 0) != expected) {
                match = false;
                break;
            }
        }

        if (match) {
            this.solved = true;
            player.playSound(clickedLocation, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
            instance.broadcast(Messages.colorize("&a&lCombination Correct! &eThe lock opened."));
            instance.checkStageProgression();
        }
    }

    @Override
    public void onReset(DungeonInstance instance) {
        this.solved = false;
        for (Location loc : dialLocations) {
            currentValues.put(loc, 0);
        }
    }
}
