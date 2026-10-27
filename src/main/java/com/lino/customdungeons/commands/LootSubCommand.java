package com.lino.customdungeons.commands;

import com.lino.customdungeons.CustomDungeons;
import com.lino.customdungeons.config.Messages;
import com.lino.customdungeons.gui.ClaimLootMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class LootSubCommand implements SubCommand {
    private final CustomDungeons plugin;

    public LootSubCommand(CustomDungeons plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "loot";
    }

    @Override
    public String getDescription() {
        return "Open the reward claim menu.";
    }

    @Override
    public String getSyntax() {
        return "/cd loot";
    }

    @Override
    public String getPermission() {
        return "customdungeons.loot";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            Messages.send(sender, "player-only");
            return;
        }

        new ClaimLootMenu(plugin, player).open();
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}
