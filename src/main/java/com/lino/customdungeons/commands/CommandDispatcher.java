package com.lino.customdungeons.commands;

import com.lino.customdungeons.CustomDungeons;
import com.lino.customdungeons.config.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.*;

public class CommandDispatcher implements CommandExecutor, TabCompleter {
    private final CustomDungeons plugin;
    private final Map<String, SubCommand> subCommands = new HashMap<>();

    public CommandDispatcher(CustomDungeons plugin) {
        this.plugin = plugin;
        register(new JoinSubCommand(plugin));
        register(new LeaveSubCommand(plugin));
        register(new ListSubCommand(plugin));
        register(new MenuSubCommand(plugin));
        register(new PartySubCommand(plugin));
        register(new QueueSubCommand(plugin));
        register(new LootSubCommand(plugin));
        register(new LoadoutSubCommand(plugin));
        register(new AdminSubCommand(plugin));
    }

    private void register(SubCommand sub) {
        subCommands.put(sub.getName().toLowerCase(), sub);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (subCommands.containsKey("menu") && sender.hasPermission("customdungeons.user.menu")) {
                subCommands.get("menu").execute(sender, args);
            } else {
                sendHelp(sender, label);
            }
            return true;
        }

        String subName = args[0].toLowerCase();
        SubCommand sub = subCommands.get(subName);
        if (sub == null) {
            sendHelp(sender, label);
            return true;
        }

        if (sub.getPermission() != null && !sender.hasPermission(sub.getPermission())) {
            Messages.send(sender, "no-permission");
            return true;
        }

        sub.execute(sender, args);
        return true;
    }

    private void sendHelp(CommandSender sender, String label) {
        sender.sendMessage(Messages.colorize("&6&m----------------&r &e&lCustomDungeons &6&m----------------"));
        for (SubCommand sub : subCommands.values()) {
            if (sub.getPermission() == null || sender.hasPermission(sub.getPermission())) {
                sender.sendMessage(Messages.colorize(" &e/" + label + " " + sub.getName() + " &7- " + sub.getDescription()));
            }
        }
        sender.sendMessage(Messages.colorize("&6&m----------------------------------------"));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> list = new ArrayList<>();
            for (SubCommand sub : subCommands.values()) {
                if (sub.getName().startsWith(args[0].toLowerCase())) {
                    if (sub.getPermission() == null || sender.hasPermission(sub.getPermission())) {
                        list.add(sub.getName());
                    }
                }
            }
            return list;
        } else if (args.length > 1) {
            SubCommand sub = subCommands.get(args[0].toLowerCase());
            if (sub != null && (sub.getPermission() == null || sender.hasPermission(sub.getPermission()))) {
                return sub.tabComplete(sender, args);
            }
        }
        return Collections.emptyList();
    }
}
