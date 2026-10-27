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
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Optional<DungeonInstance> instOpt = plugin.getDungeonManager().getInstance(player);
        if (instOpt.isEmpty()) return;

        DungeonInstance instance = instOpt.get();
        Block block = event.getClickedBlock();
        if (block == null) return;
        Location loc = block.getLocation();

        for (Structure structure : instance.getDungeon().getStructures()) {
            if (structure.getLocation() != null && structure.getLocation().equals(loc)) {
                event.setCancelled(true);
                structure.onInteract(player, instance);
                return;
            }
        }

        for (Puzzle puzzle : instance.getDungeon().getPuzzles()) {
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
}
