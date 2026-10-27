package com.lino.customdungeons.commands;

import com.lino.customdungeons.CustomDungeons;
import com.lino.customdungeons.config.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class QueueSubCommand implements SubCommand {
    private final CustomDungeons plugin;

    public QueueSubCommand(CustomDungeons plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "queue";
    }

    @Override
    public String getDescription() {
        return "Manage your matchmaking queue.";
    }

    @Override
    public String getSyntax() {
        return "/cd queue leave";
    }

    @Override
    public String getPermission() {
        return "customdungeons.queue";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            Messages.send(sender, "player-only");
            return;
        }

        if (args.length >= 2 && args[1].equalsIgnoreCase("leave")) {
            plugin.getQueueManager().dequeue(player);
        } else {
            player.sendMessage(Messages.colorize("&cUsage: " + getSyntax()));
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return List.of("leave");
        }
        return Collections.emptyList();
    }
}
