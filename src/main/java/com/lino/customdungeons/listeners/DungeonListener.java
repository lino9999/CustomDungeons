package com.lino.customdungeons.listeners;

import com.lino.customdungeons.CustomDungeons;
import com.lino.customdungeons.core.DungeonInstance;
import com.lino.customdungeons.objective.CollectItemsObjective;
import com.lino.customdungeons.objective.InteractBlockObjective;
import com.lino.customdungeons.objective.KillMobsObjective;
import com.lino.customdungeons.objective.Objective;
import com.lino.customdungeons.puzzle.Puzzle;
import com.lino.customdungeons.structure.Structure;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class DungeonListener implements Listener {
    private final CustomDungeons plugin;

    public DungeonListener(CustomDungeons plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player killer = entity.getKiller();
        if (killer == null) return;

        Optional<DungeonInstance> instOpt = plugin.getDungeonManager().getInstance(killer);
        if (instOpt.isEmpty()) return;

        DungeonInstance instance = instOpt.get();
        for (Objective obj : instance.getActiveObjectives()) {
            if (obj instanceof KillMobsObjective killObj && killObj.matches(entity.getType())) {
                killObj.increment(1);
            }
        }
        instance.checkStageProgression();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Optional<DungeonInstance> instOpt = plugin.getDungeonManager().getInstance(player);
        if (instOpt.isEmpty()) return;

        DungeonInstance instance = instOpt.get();
        event.setKeepInventory(true);
        event.setKeepLevel(true);
        event.getDrops().clear();
        event.setDroppedExp(0);

        instance.handlePlayerDeath(player);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Optional<DungeonInstance> instOpt = plugin.getDungeonManager().getInstance(player);
        if (instOpt.isEmpty()) return;

        boolean allow = plugin.getConfig().getBoolean("settings.allow-block-break", false);
        if (!allow) {
            event.setCancelled(true);
            return;
        }
        instOpt.get().getRollbackManager().recordChange(event.getBlock().getLocation(), event.getBlock().getBlockData());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Optional<DungeonInstance> instOpt = plugin.getDungeonManager().getInstance(player);
        if (instOpt.isEmpty()) return;

        boolean allow = plugin.getConfig().getBoolean("settings.allow-block-place", false);
        if (!allow) {
            event.setCancelled(true);
            return;
        }
        instOpt.get().getRollbackManager().recordChange(event.getBlock().getLocation(), event.getBlockReplacedState().getBlockData());
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onItemPickup(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player) {
            Optional<DungeonInstance> instOpt = plugin.getDungeonManager().getInstance(player);
            if (instOpt.isEmpty()) return;

            DungeonInstance instance = instOpt.get();
            ItemStack item = event.getItem().getItemStack();
            for (Objective obj : instance.getActiveObjectives()) {
                if (obj instanceof CollectItemsObjective collectObj && collectObj.matches(item.getType())) {
                    collectObj.increment(item.getAmount());
                }
            }
            instance.checkStageProgression();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommand(org.bukkit.event.player.PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        Optional<DungeonInstance> instOpt = plugin.getDungeonManager().getInstance(player);
        if (instOpt.isEmpty()) return;

        if (player.hasPermission("customdungeons.admin.*")) return;

        String msg = event.getMessage().toLowerCase();
        if (msg.startsWith("/cd ") || msg.startsWith("/customdungeons ") || msg.startsWith("/dungeon ") || msg.startsWith("/dungeons ")) {
            return;
        }

        event.setCancelled(true);
        player.sendMessage(com.lino.customdungeons.config.Messages.colorize("&cYou cannot use server commands while inside a dungeon! Use &e/cd leave &cto exit."));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDropItem(org.bukkit.event.player.PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Optional<DungeonInstance> instOpt = plugin.getDungeonManager().getInstance(player);
        if (instOpt.isEmpty()) return;

        if (!plugin.getConfig().getBoolean("settings.allow-drop-items", false)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamage(org.bukkit.event.entity.EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            Optional<DungeonInstance> instOpt = plugin.getDungeonManager().getInstance(player);
            if (instOpt.isPresent() && instOpt.get().getState() != com.lino.customdungeons.core.DungeonState.ACTIVE) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player victim && event.getDamager() instanceof Player) {
            Optional<DungeonInstance> instOpt = plugin.getDungeonManager().getInstance(victim);
            if (instOpt.isPresent() && !plugin.getConfig().getBoolean("settings.allow-pvp", false)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityExplode(org.bukkit.event.entity.EntityExplodeEvent event) {
        handleExplosion(event.getLocation(), event.blockList());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockExplode(org.bukkit.event.block.BlockExplodeEvent event) {
        handleExplosion(event.getBlock().getLocation(), event.blockList());
    }

    private void handleExplosion(Location origin, java.util.List<Block> blocks) {
        for (DungeonInstance instance : plugin.getDungeonManager().getActiveInstances()) {
            Location pos1 = instance.getDungeon().getPos1();
            Location pos2 = instance.getDungeon().getPos2();
            if (pos1 != null && pos2 != null && isWithinBounds(origin, pos1, pos2)) {
                boolean allow = plugin.getConfig().getBoolean("settings.allow-block-break", false);
                if (!allow) {
                    blocks.clear();
                } else {
                    for (Block b : blocks) {
                        instance.getRollbackManager().recordChange(b.getLocation(), b.getBlockData());
                    }
                }
                break;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == org.bukkit.GameMode.SPECTATOR) {
            event.setCancelled(true);
            return;
        }

        Optional<DungeonInstance> instOpt = plugin.getDungeonManager().getInstance(player);
        if (instOpt.isEmpty()) return;

        DungeonInstance instance = instOpt.get();
        Block block = event.getClickedBlock();
        if (block == null) return;
        Location loc = block.getLocation();

        for (Structure structure : instance.getStructures()) {
            if (structure.getLocation() != null && structure.getLocation().equals(loc)) {
                event.setCancelled(true);
                structure.onInteract(player, instance);
                return;
            }
        }

        for (Puzzle puzzle : instance.getPuzzles()) {
            puzzle.onInteract(player, loc, instance);
        }

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            for (Objective obj : instance.getActiveObjectives()) {
                if (obj instanceof InteractBlockObjective interactObj && interactObj.matches(block.getType())) {
                    interactObj.increment(1);
                }
            }
            instance.checkStageProgression();
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerMove(org.bukkit.event.player.PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();
        if (to == null || (from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ())) {
            return;
        }

        Player player = event.getPlayer();
        Optional<DungeonInstance> instOpt = plugin.getDungeonManager().getInstance(player);
        if (instOpt.isEmpty()) return;

        DungeonInstance instance = instOpt.get();
        com.lino.customdungeons.core.Dungeon dungeon = instance.getDungeon();
        Location pos1 = dungeon.getPos1();
        Location pos2 = dungeon.getPos2();
        if (pos1 != null && pos2 != null) {
            if (!isWithinBounds(to, pos1, pos2)) {
                event.setCancelled(true);
                player.sendMessage(com.lino.customdungeons.config.Messages.colorize("&cYou cannot leave the dungeon arena boundaries!"));
                return;
            }
        }

        for (Objective obj : instance.getActiveObjectives()) {
            if (obj instanceof com.lino.customdungeons.objective.ReachLocationObjective reachObj && reachObj.isWithin(to)) {
                reachObj.increment(1);
            }
        }
        instance.checkStageProgression();
    }

    private boolean isWithinBounds(Location loc, Location pos1, Location pos2) {
        if (loc.getWorld() == null || pos1.getWorld() == null || pos2.getWorld() == null) return true;
        if (!loc.getWorld().equals(pos1.getWorld()) || !loc.getWorld().equals(pos2.getWorld())) return false;
        double minX = Math.min(pos1.getX(), pos2.getX());
        double maxX = Math.max(pos1.getX(), pos2.getX());
        double minY = Math.min(pos1.getY(), pos2.getY());
        double maxY = Math.max(pos1.getY(), pos2.getY());
        double minZ = Math.min(pos1.getZ(), pos2.getZ());
        double maxZ = Math.max(pos1.getZ(), pos2.getZ());
        return loc.getX() >= minX && loc.getX() <= maxX &&
               loc.getY() >= minY && loc.getY() <= maxY &&
               loc.getZ() >= minZ && loc.getZ() <= maxZ;
    }
}
