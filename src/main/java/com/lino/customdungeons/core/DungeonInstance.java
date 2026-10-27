package com.lino.customdungeons.core;

import com.lino.customdungeons.CustomDungeons;
import com.lino.customdungeons.api.events.DungeonCompleteEvent;
import com.lino.customdungeons.api.events.DungeonFailEvent;
import com.lino.customdungeons.api.events.StageAdvanceEvent;
import com.lino.customdungeons.config.Messages;
import com.lino.customdungeons.loot.LootTable;
import com.lino.customdungeons.objective.Objective;
import com.lino.customdungeons.objective.SurviveTimeObjective;
import com.lino.customdungeons.rollback.RollbackManager;
import com.lino.customdungeons.structure.SpawnerStructure;
import com.lino.customdungeons.structure.Structure;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class DungeonInstance {
    private final UUID instanceId = UUID.randomUUID();
    private final CustomDungeons plugin;
    private final Dungeon dungeon;
    private final Set<UUID> participants = new HashSet<>();
    private final Set<UUID> alivePlayers = new HashSet<>();
    private final Map<UUID, Integer> playerLives = new HashMap<>();
    private final Map<UUID, PlayerSnapshot> savedSnapshots = new HashMap<>();
    private final List<LivingEntity> spawnedEntities = new ArrayList<>();
    private final RollbackManager rollbackManager = new RollbackManager();
    private final List<Structure> structures = new ArrayList<>();
    private final List<com.lino.customdungeons.puzzle.Puzzle> puzzles = new ArrayList<>();
    private final Set<UUID> selectedLoadouts = new HashSet<>();

    private DungeonState state = DungeonState.WAITING;
    private int currentStageIndex = 1;
    private DungeonStage currentStage;
    private List<Objective> activeObjectives = new ArrayList<>();
    private int secondsRemaining;
    private BukkitTask tickerTask;
    private BukkitTask countdownTask;

    public DungeonInstance(CustomDungeons plugin, Dungeon dungeon, List<Player> players) {
        this.plugin = plugin;
        this.dungeon = dungeon;
        this.secondsRemaining = dungeon.getTimeLimitSeconds();

        for (Player p : players) {
            participants.add(p.getUniqueId());
            alivePlayers.add(p.getUniqueId());
            playerLives.put(p.getUniqueId(), dungeon.getLives());
        }

        for (Structure s : dungeon.getStructures()) {
            this.structures.add(s.copy());
        }
        for (com.lino.customdungeons.puzzle.Puzzle p : dungeon.getPuzzles()) {
            this.puzzles.add(p.copy());
        }
    }

    public void start() {
        this.state = DungeonState.STARTING;
        Location lobby = dungeon.getLobbyLocation() != null ? dungeon.getLobbyLocation() : dungeon.getSpawnLocation();
        for (UUID uuid : participants) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) {
                savedSnapshots.put(uuid, new PlayerSnapshot(p));
                if (lobby != null) p.teleport(lobby);
            }
        }

        final int[] countdown = {5};
        this.countdownTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (countdown[0] > 0) {
                for (UUID uuid : participants) {
                    Player p = Bukkit.getPlayer(uuid);
                    if (p != null) {
                        Messages.send(p, "dungeon.countdown", Map.of("seconds", String.valueOf(countdown[0])));
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1.0f, 1.0f);
                    }
                }
                countdown[0]--;
            } else {
                if (countdownTask != null) {
                    countdownTask.cancel();
                    countdownTask = null;
                }
                beginActiveDungeon();
            }
        }, 0L, 20L);
    }

    private void beginActiveDungeon() {
        this.state = DungeonState.ACTIVE;
        Location spawn = dungeon.getSpawnLocation() != null ? dungeon.getSpawnLocation() : Bukkit.getWorlds().get(0).getSpawnLocation();
        for (UUID uuid : participants) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) {
                p.teleport(spawn);
                p.setGameMode(GameMode.SURVIVAL);
                p.setHealth(p.getMaxHealth());
                p.setFoodLevel(20);
                Messages.send(p, "dungeon.started");
                p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 0.8f, 1.0f);
            }
        }
        loadStage(1);
        startTicker();
    }

    private void startTicker() {
        this.tickerTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (state != DungeonState.ACTIVE) return;

            secondsRemaining--;
            if (secondsRemaining <= 0) {
                fail("Time expired.");
                return;
            }

            for (Structure s : structures) {
                if (s instanceof SpawnerStructure spawner) {
                    spawner.tick(this);
                }
            }

            for (Objective obj : activeObjectives) {
                if (obj instanceof SurviveTimeObjective survive) {
                    survive.increment(1);
                }
            }
            checkStageProgression();
        }, 20L, 20L);
    }

    public void loadStage(int stageNumber) {
        this.currentStageIndex = stageNumber;
        this.currentStage = dungeon.getStages().get(stageNumber);
        if (currentStage == null) {
            complete();
            return;
        }

        this.activeObjectives = currentStage.createFreshObjectives();
        Bukkit.getPluginManager().callEvent(new StageAdvanceEvent(this, currentStage, stageNumber));

        for (UUID uuid : participants) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) {
                p.sendMessage(Messages.colorize("&e&m----------------------------------------"));
                p.sendMessage(Messages.colorize("&6&lSTAGE " + stageNumber + ": &e" + currentStage.getName()));
                for (Objective obj : activeObjectives) {
                    p.sendMessage(Messages.colorize(" &7- " + obj.getProgressBar()));
                }
                p.sendMessage(Messages.colorize("&e&m----------------------------------------"));
                p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.7f, 1.2f);
            }
        }
    }

    public synchronized void checkStageProgression() {
        if (state != DungeonState.ACTIVE || currentStage == null) return;

        boolean allDone = true;
        for (Objective obj : activeObjectives) {
            if (!obj.isCompleted()) {
                allDone = false;
                break;
            }
        }

        if (allDone) {
            for (String cmd : currentStage.getClearCommands()) {
                for (UUID uuid : participants) {
                    Player p = Bukkit.getPlayer(uuid);
                    if (p != null) {
                        String parsed = cmd.replace("{player}", p.getName());
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parsed);
                    }
                }
            }

            int nextStage = currentStageIndex + 1;
            if (dungeon.getStages().containsKey(nextStage)) {
                broadcast(Messages.get("dungeon.stage-cleared", Map.of("stage", String.valueOf(currentStageIndex))));
                loadStage(nextStage);
            } else {
                complete();
            }
        }
    }

    public void handlePlayerDeath(Player player) {
        UUID uuid = player.getUniqueId();
        int lives = playerLives.getOrDefault(uuid, 1) - 1;
        playerLives.put(uuid, lives);

        if (lives > 0) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (player.isOnline()) {
                    player.spigot().respawn();
                    Location spawn = dungeon.getSpawnLocation() != null ? dungeon.getSpawnLocation() : Bukkit.getWorlds().get(0).getSpawnLocation();
                    player.teleport(spawn);
                    player.setHealth(player.getMaxHealth());
                    player.setFoodLevel(20);
                }
            }, 1L);
            broadcast(Messages.get("dungeon.player-died", Map.of("player", player.getName(), "lives", String.valueOf(lives))));
        } else {
            alivePlayers.remove(uuid);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (player.isOnline()) {
                    player.spigot().respawn();
                    player.setGameMode(GameMode.SPECTATOR);
                    Location spawn = dungeon.getSpawnLocation() != null ? dungeon.getSpawnLocation() : Bukkit.getWorlds().get(0).getSpawnLocation();
                    player.teleport(spawn);
                }
            }, 1L);
            Messages.send(player, "dungeon.eliminated");

            if (alivePlayers.isEmpty()) {
                fail("All players ran out of lives.");
            }
        }
    }

    public void leavePlayer(Player player) {
        UUID uuid = player.getUniqueId();
        participants.remove(uuid);
        alivePlayers.remove(uuid);

        boolean isolated = "ISOLATED".equalsIgnoreCase(plugin.getConfig().getString("settings.inventory-handling", "ISOLATED"));
        PlayerSnapshot snapshot = savedSnapshots.remove(uuid);
        if (isolated && snapshot != null) {
            snapshot.restore(player);
        }

        Location exit = dungeon.getExitLocation() != null ? dungeon.getExitLocation() : Bukkit.getWorlds().get(0).getSpawnLocation();
        player.teleport(exit);
        player.setGameMode(GameMode.SURVIVAL);

        plugin.getDungeonManager().removePlayerFromInstance(player, this);

        broadcast(Messages.colorize("&e" + player.getName() + " &chas left the dungeon."));

        if (alivePlayers.isEmpty()) {
            fail("All players have left the dungeon.");
        }
    }

    public void complete() {
        this.state = DungeonState.COMPLETED;
        stopAllTasks();
        Bukkit.getPluginManager().callEvent(new DungeonCompleteEvent(this));

        for (UUID uuid : participants) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) {
                p.sendTitle(Messages.get("dungeon.victory-title"), Messages.get("dungeon.victory-subtitle", Map.of("dungeon", dungeon.getDisplayName())), 10, 70, 20);
                p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);

                if (dungeon.getRewardLootTable() != null) {
                    Optional<LootTable> table = plugin.getLootManager().getTable(dungeon.getRewardLootTable());
                    table.ifPresent(lootTable -> {
                        List<ItemStack> items = lootTable.generateLoot();
                        plugin.getLootManager().addPendingRewards(p, items);
                        p.sendMessage(Messages.colorize("&aYour dungeon rewards are ready! Use &e/cd loot &ato claim them."));
                    });
                }
            }
        }

        Bukkit.getScheduler().runTaskLater(plugin, this::cleanup, 100L);
    }

    public void fail(String reason) {
        this.state = DungeonState.FAILED;
        stopAllTasks();
        Bukkit.getPluginManager().callEvent(new DungeonFailEvent(this, reason));

        for (UUID uuid : participants) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) {
                p.sendTitle(Messages.get("dungeon.defeat-title"), Messages.get("dungeon.defeat-subtitle"), 10, 70, 20);
                p.playSound(p.getLocation(), Sound.ENTITY_WITHER_DEATH, 0.8f, 0.8f);
            }
        }

        Bukkit.getScheduler().runTaskLater(plugin, this::cleanup, 100L);
    }

    public void cleanup() {
        this.state = DungeonState.RESETTING;
        stopAllTasks();
        Location exit = dungeon.getExitLocation() != null ? dungeon.getExitLocation() : Bukkit.getWorlds().get(0).getSpawnLocation();

        boolean isolated = "ISOLATED".equalsIgnoreCase(plugin.getConfig().getString("settings.inventory-handling", "ISOLATED"));

        for (UUID uuid : participants) {
            Player p = Bukkit.getPlayer(uuid);
            PlayerSnapshot snapshot = savedSnapshots.get(uuid);
            if (p != null && p.isOnline()) {
                if (isolated && snapshot != null) {
                    snapshot.restore(p);
                }
                p.teleport(exit);
                p.setGameMode(GameMode.SURVIVAL);

                if (state == DungeonState.COMPLETED) {
                    if (dungeon.getRewardExperience() > 0) {
                        p.giveExp(dungeon.getRewardExperience());
                    }
                    for (String cmd : dungeon.getRewardCommands()) {
                        String parsed = cmd.replace("{player}", p.getName());
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parsed);
                    }
                }
            } else if (isolated && snapshot != null) {
                plugin.getDungeonManager().saveOfflineRestore(uuid, snapshot);
            }
        }

        for (LivingEntity e : spawnedEntities) {
            if (e.isValid()) {
                e.remove();
            }
        }
        spawnedEntities.clear();

        for (Structure s : structures) {
            s.onReset(this);
        }
        for (com.lino.customdungeons.puzzle.Puzzle p : puzzles) {
            p.onReset(this);
        }

        rollbackManager.performRollback();
        plugin.getDungeonManager().removeInstance(this);
    }

    public boolean canSelectLoadout(Player player) {
        return !selectedLoadouts.contains(player.getUniqueId());
    }

    public void markLoadoutSelected(Player player) {
        selectedLoadouts.add(player.getUniqueId());
    }

    public List<Structure> getStructures() {
        return structures;
    }

    public List<com.lino.customdungeons.puzzle.Puzzle> getPuzzles() {
        return puzzles;
    }

    private void stopAllTasks() {
        if (tickerTask != null) {
            tickerTask.cancel();
            tickerTask = null;
        }
        if (countdownTask != null) {
            countdownTask.cancel();
            countdownTask = null;
        }
    }

    public void trackEntity(LivingEntity entity) {
        spawnedEntities.add(entity);
    }

    public void broadcast(String message) {
        for (UUID uuid : participants) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) {
                p.sendMessage(message);
            }
        }
    }

    public UUID getInstanceId() {
        return instanceId;
    }

    public CustomDungeons getPlugin() {
        return plugin;
    }

    public Dungeon getDungeon() {
        return dungeon;
    }

    public DungeonState getState() {
        return state;
    }

    public Set<UUID> getParticipants() {
        return Collections.unmodifiableSet(participants);
    }

    public boolean isParticipant(Player player) {
        return participants.contains(player.getUniqueId());
    }

    public List<Objective> getActiveObjectives() {
        return activeObjectives;
    }

    public RollbackManager getRollbackManager() {
        return rollbackManager;
    }

    public int getSecondsRemaining() {
        return secondsRemaining;
    }

    public int getLives(Player player) {
        return playerLives.getOrDefault(player.getUniqueId(), 0);
    }
}
