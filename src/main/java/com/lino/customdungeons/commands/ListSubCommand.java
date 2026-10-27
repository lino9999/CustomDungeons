package com.lino.customdungeons.commands;

import com.lino.customdungeons.CustomDungeons;
import com.lino.customdungeons.config.Messages;
import com.lino.customdungeons.core.Dungeon;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ListSubCommand implements SubCommand {
    private final CustomDungeons plugin;

    public ListSubCommand(CustomDungeons plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String getDescription() {
        return "List all available dungeons.";
    }

    @Override
    public String getSyntax() {
        return "/cd list";
    }

    @Override
    public String getPermission() {
        return "customdungeons.user.list";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Collection<Dungeon> list = plugin.getDungeonManager().getDungeons();
        if (list.isEmpty()) {
            sender.sendMessage(Messages.colorize("&7No dungeons currently loaded."));
            return;
        }

        sender.sendMessage(Messages.colorize("&6&lAvailable Dungeons:"));
        for (Dungeon d : list) {
            String status = d.isEnabled() ? "&a[Enabled]" : "&c[Disabled]";
            sender.sendMessage(Messages.colorize(" &8- " + d.getDisplayName() + " &7(" + d.getId() + ") " + status));
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}
