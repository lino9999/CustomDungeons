package com.lino.customdungeons.core;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PlayerSnapshot {
    private final ItemStack[] contents;
    private final ItemStack[] armor;
    private final ItemStack offHand;
    private final int level;
    private final float exp;
    private final double health;
    private final int foodLevel;
    private final GameMode gameMode;
    private final Location returnLocation;
    private final List<PotionEffect> activePotionEffects;

    public PlayerSnapshot(Player player) {
        ItemStack[] origContents = player.getInventory().getContents();
        this.contents = new ItemStack[origContents.length];
        for (int i = 0; i < origContents.length; i++) {
            this.contents[i] = origContents[i] != null ? origContents[i].clone() : null;
        }

        ItemStack[] origArmor = player.getInventory().getArmorContents();
        this.armor = new ItemStack[origArmor.length];
        for (int i = 0; i < origArmor.length; i++) {
            this.armor[i] = origArmor[i] != null ? origArmor[i].clone() : null;
        }

        this.offHand = player.getInventory().getItemInOffHand() != null ? player.getInventory().getItemInOffHand().clone() : null;
        this.level = player.getLevel();
        this.exp = player.getExp();
        this.health = player.getHealth();
        this.foodLevel = player.getFoodLevel();
        this.gameMode = player.getGameMode();
        this.returnLocation = player.getLocation().clone();
        this.activePotionEffects = new ArrayList<>(player.getActivePotionEffects());
    }

    public void restore(Player player) {
        player.getInventory().setContents(contents);
        player.getInventory().setArmorContents(armor);
        player.getInventory().setItemInOffHand(offHand);
        player.setLevel(level);
        player.setExp(exp);
        player.setGameMode(gameMode);
        player.setFoodLevel(foodLevel);
        if (health > 0 && health <= player.getMaxHealth()) {
            player.setHealth(health);
        }

        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        for (PotionEffect effect : activePotionEffects) {
            player.addPotionEffect(effect);
        }
    }

    public Location getReturnLocation() {
        return returnLocation;
    }
}
