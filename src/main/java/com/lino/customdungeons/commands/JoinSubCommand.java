package com.lino.customdungeons.commands;

import com.lino.customdungeons.CustomDungeons;
import com.lino.customdungeons.config.Messages;
import com.lino.customdungeons.core.Dungeon;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class JoinSubCommand implements SubCommand {
    private final CustomDungeons plugin;

    public JoinSubCommand(CustomDungeons plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "join";
    }

    @Override
    public String getDescription() {
        return "Join a dungeon or its matchmaking queue.";
    }

    @Override
    public String getSyntax() {
        return "/cd join <dungeon>";
    }

    @Override
    public String getPermission() {
        return "customdungeons.user.join";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            Messages.send(sender, "player-only");
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(Messages.colorize("&cUsage: " + getSyntax()));
            return;
        }

        String dungeonId = args[1].toLowerCase();
        Optional<Dungeon> dungeonOpt = plugin.getDungeonManager().getDungeon(dungeonId);
        if (dungeonOpt.isEmpty()) {
            Messages.send(player, "dungeon.not-found", Map.of("dungeon", dungeonId));
            return;
        }

        Dungeon dungeon = dungeonOpt.get();
        if (plugin.getDungeonManager().isInDungeon(player)) {
            Messages.send(player, "dungeon.already-in-game");
            return;
        }

        if (!plugin.getDungeonManager().canPlayerJoin(player, dungeon)) {
            Messages.send(player, "dungeon.cannot-join", Map.of("reason", "Requirements not met."));
            return;
        }

        plugin.getQueueManager().enqueue(player, dungeon.getId());
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            List<String> list = new ArrayList<>();
            for (Dungeon d : plugin.getDungeonManager().getDungeons()) {
                if (d.getId().startsWith(args[1].toLowerCase())) {
                    list.add(d.getId());
                }
            }
            return list;
        }
        return Collections.emptyList();
    }
}
