package com.lino.customdungeons.commands;

import com.lino.customdungeons.CustomDungeons;
import com.lino.customdungeons.config.Messages;
import com.lino.customdungeons.gui.LoadoutMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class LoadoutSubCommand implements SubCommand {
    private final CustomDungeons plugin;

    public LoadoutSubCommand(CustomDungeons plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "loadout";
    }

    @Override
    public String getDescription() {
        return "Select a combat class loadout.";
    }

    @Override
    public String getSyntax() {
        return "/cd loadout";
    }

    @Override
    public String getPermission() {
        return "customdungeons.loadout";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            Messages.send(sender, "player-only");
            return;
        }

        if (!plugin.getDungeonManager().isInDungeon(player)) {
            player.sendMessage(com.lino.customdungeons.config.Messages.colorize("&cYou can only select a loadout while inside a dungeon!"));
            return;
        }

        new LoadoutMenu(plugin, player).open();
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}
