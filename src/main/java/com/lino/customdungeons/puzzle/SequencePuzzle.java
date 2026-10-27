package com.lino.customdungeons.puzzle;

import com.lino.customdungeons.config.Messages;
import com.lino.customdungeons.core.DungeonInstance;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SequencePuzzle extends Puzzle {
    private final List<Location> targetSequence;
    private final List<Location> currentProgress = new ArrayList<>();

    public SequencePuzzle(String id, List<Location> targetSequence) {
        super(id, PuzzleType.SEQUENCE);
        this.targetSequence = targetSequence;
    }

    @Override
    public void onInteract(Player player, Location clickedLocation, DungeonInstance instance) {
        if (solved) return;
        int nextIndex = currentProgress.size();
        if (nextIndex < targetSequence.size() && targetSequence.get(nextIndex).equals(clickedLocation)) {
            currentProgress.add(clickedLocation);
            player.playSound(clickedLocation, Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.2f + (nextIndex * 0.2f));
            if (currentProgress.size() == targetSequence.size()) {
                this.solved = true;
                player.playSound(clickedLocation, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
                instance.broadcast(Messages.colorize("&a&lPuzzle Solved! &eThe sequence is complete."));
                instance.checkStageProgression();
            }
        } else {
            currentProgress.clear();
            player.playSound(clickedLocation, Sound.BLOCK_ANVIL_LAND, 0.6f, 0.5f);
            player.sendMessage(Messages.colorize("&cWrong button! The sequence reset."));
        }
    }

    @Override
    public void onReset(DungeonInstance instance) {
        this.solved = false;
        this.currentProgress.clear();
    }
}
