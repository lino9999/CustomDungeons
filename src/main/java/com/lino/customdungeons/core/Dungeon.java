package com.lino.customdungeons.core;

import com.lino.customdungeons.config.Messages;
import com.lino.customdungeons.puzzle.Puzzle;
import com.lino.customdungeons.structure.Structure;
import org.bukkit.Location;

import java.util.*;

public class Dungeon {
    private final String id;
    private boolean enabled = true;
    private String displayName;
    private String description;
    private int minPlayers = 1;
    private int maxPlayers = 4;
    private int lives = 3;
    private int timeLimitSeconds = 600;

    private Location lobbyLocation;
    private Location spawnLocation;
    private Location exitLocation;
    private Location pos1;
    private Location pos2;

    private String requiredPermission;
    private int requiredLevel = 0;
    private double requiredCost = 0.0;

    private List<String> rewardCommands = new ArrayList<>();
    private int rewardExperience = 0;
    private String rewardLootTable;

    private final Map<Integer, DungeonStage> stages = new TreeMap<>();
    private final List<Structure> structures = new ArrayList<>();
    private final List<Puzzle> puzzles = new ArrayList<>();

    public Dungeon(String id) {
        this.id = id.toLowerCase();
        this.displayName = "&6" + id;
        this.description = "&7A mysterious dungeon challenge.";
    }

    public String getId() {
        return id;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getDisplayName() {
        return Messages.colorize(displayName);
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return Messages.colorize(description);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public int getTimeLimitSeconds() {
        return timeLimitSeconds;
    }

    public void setTimeLimitSeconds(int timeLimitSeconds) {
        this.timeLimitSeconds = timeLimitSeconds;
    }

    public Location getLobbyLocation() {
        return lobbyLocation;
    }

    public void setLobbyLocation(Location lobbyLocation) {
        this.lobbyLocation = lobbyLocation;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public void setSpawnLocation(Location spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

    public Location getExitLocation() {
        return exitLocation;
    }

    public void setExitLocation(Location exitLocation) {
        this.exitLocation = exitLocation;
    }

    public Location getPos1() {
        return pos1;
    }

    public void setPos1(Location pos1) {
        this.pos1 = pos1;
    }

    public Location getPos2() {
        return pos2;
    }

    public void setPos2(Location pos2) {
        this.pos2 = pos2;
    }

    public String getRequiredPermission() {
        return requiredPermission;
    }

    public void setRequiredPermission(String requiredPermission) {
        this.requiredPermission = requiredPermission;
    }

    public int getRequiredLevel() {
        return requiredLevel;
    }

    public void setRequiredLevel(int requiredLevel) {
        this.requiredLevel = requiredLevel;
    }

    public double getRequiredCost() {
        return requiredCost;
    }

    public void setRequiredCost(double requiredCost) {
        this.requiredCost = requiredCost;
    }

    public List<String> getRewardCommands() {
        return rewardCommands;
    }

    public void setRewardCommands(List<String> rewardCommands) {
        this.rewardCommands = rewardCommands != null ? rewardCommands : new ArrayList<>();
    }

    public int getRewardExperience() {
        return rewardExperience;
    }

    public void setRewardExperience(int rewardExperience) {
        this.rewardExperience = rewardExperience;
    }

    public String getRewardLootTable() {
        return rewardLootTable;
    }

    public void setRewardLootTable(String rewardLootTable) {
        this.rewardLootTable = rewardLootTable;
    }

    public Map<Integer, DungeonStage> getStages() {
        return stages;
    }

    public void addStage(DungeonStage stage) {
        this.stages.put(stage.getStageNumber(), stage);
    }

    public List<Structure> getStructures() {
        return structures;
    }

    public void addStructure(Structure structure) {
        this.structures.add(structure);
    }

    public List<Puzzle> getPuzzles() {
        return puzzles;
    }

    public void addPuzzle(Puzzle puzzle) {
        this.puzzles.add(puzzle);
    }
}
