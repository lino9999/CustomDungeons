package com.lino.customdungeons.commands;

import com.lino.customdungeons.CustomDungeons;
import com.lino.customdungeons.config.Messages;
import com.lino.customdungeons.core.DungeonInstance;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class LeaveSubCommand implements SubCommand {
    private final CustomDungeons plugin;

    public LeaveSubCommand(CustomDungeons plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "leave";
    }

    @Override
    public String getDescription() {
        return "Leave your current dungeon.";
    }

    @Override
    public String getSyntax() {
        return "/cd leave";
    }

    @Override
    public String getPermission() {
        return "customdungeons.user.leave";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            Messages.send(sender, "player-only");
            return;
        }

        Optional<DungeonInstance> instOpt = plugin.getDungeonManager().getInstance(player);
        if (instOpt.isEmpty()) {
            Messages.send(player, "dungeon.not-in-game");
            return;
        }

        instOpt.get().handlePlayerDeath(player);
        Messages.send(player, "dungeon.left");
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}
