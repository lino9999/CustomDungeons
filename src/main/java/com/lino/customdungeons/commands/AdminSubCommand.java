package com.lino.customdungeons.commands;

import com.lino.customdungeons.CustomDungeons;
import com.lino.customdungeons.config.Messages;
import com.lino.customdungeons.core.Dungeon;
import com.lino.customdungeons.core.DungeonInstance;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;

public class AdminSubCommand implements SubCommand {
    private final CustomDungeons plugin;

    public AdminSubCommand(CustomDungeons plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "admin";
    }

    @Override
    public String getDescription() {
        return "Administrative controls and configuration.";
    }

    @Override
    public String getSyntax() {
        return "/cd admin <reload|create|delete|setlobby|setspawn|setexit|setpos1|setpos2|start|stop|stats>";
    }

    @Override
    public String getPermission() {
        return "customdungeons.admin.*";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Messages.colorize("&cUsage: " + getSyntax()));
            return;
        }

        String sub = args[1].toLowerCase();
        switch (sub) {
            case "reload": {
                plugin.getConfigManager().reload();
                Messages.send(sender, "reload-success");
                break;
            }
            case "create": {
                if (args.length < 3) {
                    sender.sendMessage(Messages.colorize("&cUsage: /cd admin create <name>"));
                    return;
                }
                String name = args[2].toLowerCase();
                File file = new File(plugin.getConfigManager().getDungeonsFolder(), name + ".yml");
                if (file.exists()) {
                    sender.sendMessage(Messages.colorize("&cA dungeon with name '" + name + "' already exists!"));
                    return;
                }
                try {
                    YamlConfiguration yaml = new YamlConfiguration();
                    yaml.set("enabled", true);
                    yaml.set("display-name", "&6" + name);
                    yaml.set("description", "A custom dungeon created via commands.");
                    yaml.set("min-players", 1);
                    yaml.set("max-players", 4);
                    yaml.set("lives", 3);
                    yaml.set("time-limit-seconds", 600);
                    yaml.save(file);
                    plugin.getDungeonManager().load();
                    Messages.send(sender, "admin.created", Map.of("dungeon", name));
                } catch (Exception e) {
                    sender.sendMessage(Messages.colorize("&cFailed to save dungeon file: " + e.getMessage()));
                }
                break;
            }
            case "delete": {
                if (args.length < 3) {
                    sender.sendMessage(Messages.colorize("&cUsage: /cd admin delete <name>"));
                    return;
                }
                String name = args[2].toLowerCase();
                File file = new File(plugin.getConfigManager().getDungeonsFolder(), name + ".yml");
                if (file.exists() && file.delete()) {
                    plugin.getDungeonManager().load();
                    Messages.send(sender, "admin.deleted", Map.of("dungeon", name));
                } else {
                    Messages.send(sender, "dungeon.not-found", Map.of("dungeon", name));
                }
                break;
            }
            case "setlobby":
            case "setspawn":
            case "setexit":
            case "setpos1":
            case "setpos2": {
                if (!(sender instanceof Player player)) {
                    Messages.send(sender, "player-only");
                    return;
                }
                if (args.length < 3) {
                    player.sendMessage(Messages.colorize("&cUsage: /cd admin " + sub + " <dungeon>"));
                    return;
                }
                String dungeonName = args[2].toLowerCase();
                File file = new File(plugin.getConfigManager().getDungeonsFolder(), dungeonName + ".yml");
                if (!file.exists()) {
                    Messages.send(player, "dungeon.not-found", Map.of("dungeon", dungeonName));
                    return;
                }
                try {
                    YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
                    String pointKey = sub.replace("set", "");
                    yaml.set("locations." + pointKey, plugin.getDungeonManager().serializeLocation(player.getLocation()));
                    yaml.save(file);
                    plugin.getDungeonManager().load();
                    Messages.send(player, "admin.setup-point", Map.of("point", pointKey, "dungeon", dungeonName));
                } catch (Exception e) {
                    player.sendMessage(Messages.colorize("&cError saving location: " + e.getMessage()));
                }
                break;
            }
            case "start": {
                if (!(sender instanceof Player player)) {
                    Messages.send(sender, "player-only");
                    return;
                }
                if (args.length < 3) {
                    player.sendMessage(Messages.colorize("&cUsage: /cd admin start <dungeon>"));
                    return;
                }
                String name = args[2].toLowerCase();
                Optional<Dungeon> dOpt = plugin.getDungeonManager().getDungeon(name);
                if (dOpt.isEmpty()) {
                    Messages.send(player, "dungeon.not-found", Map.of("dungeon", name));
                    return;
                }
                plugin.getDungeonManager().startInstance(dOpt.get(), Collections.singletonList(player));
                break;
            }
            case "stop": {
                if (args.length < 3) {
                    sender.sendMessage(Messages.colorize("&cUsage: /cd admin stop <dungeon>"));
                    return;
                }
                String name = args[2].toLowerCase();
                boolean found = false;
                for (DungeonInstance inst : plugin.getDungeonManager().getActiveInstances()) {
                    if (inst.getDungeon().getId().equalsIgnoreCase(name)) {
                        inst.fail("Stopped by administrator.");
                        found = true;
                    }
                }
                if (found) {
                    Messages.send(sender, "admin.forced-stop", Map.of("dungeon", name));
                } else {
                    sender.sendMessage(Messages.colorize("&cNo active instance found for '" + name + "'."));
                }
                break;
            }
            case "stats": {
                sender.sendMessage(Messages.colorize("&6&lCustomDungeons System Statistics:"));
                sender.sendMessage(Messages.colorize(" &eLoaded Blueprints: &f" + plugin.getDungeonManager().getDungeons().size()));
                sender.sendMessage(Messages.colorize(" &eActive Dungeon Instances: &f" + plugin.getDungeonManager().getActiveInstances().size()));
                sender.sendMessage(Messages.colorize(" &eLoaded Classes: &f" + plugin.getLoadoutManager().getLoadouts().size()));
                break;
            }
            default:
                sender.sendMessage(Messages.colorize("&cUnknown subcommand. " + getSyntax()));
                break;
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return List.of("reload", "create", "delete", "setlobby", "setspawn", "setexit", "setpos1", "setpos2", "start", "stop", "stats");
        }
        if (args.length == 3) {
            List<String> list = new ArrayList<>();
            for (Dungeon d : plugin.getDungeonManager().getDungeons()) {
                if (d.getId().startsWith(args[2].toLowerCase())) {
                    list.add(d.getId());
                }
            }
            return list;
        }
        return Collections.emptyList();
    }
}
