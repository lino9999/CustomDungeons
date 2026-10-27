package com.lino.customdungeons.commands;

import com.lino.customdungeons.CustomDungeons;
import com.lino.customdungeons.config.Messages;
import com.lino.customdungeons.gui.PartyMenu;
import com.lino.customdungeons.party.Party;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class PartySubCommand implements SubCommand {
    private final CustomDungeons plugin;

    public PartySubCommand(CustomDungeons plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "party";
    }

    @Override
    public String getDescription() {
        return "Manage your dungeon party.";
    }

    @Override
    public String getSyntax() {
        return "/cd party [create|invite|accept|leave|disband|menu]";
    }

    @Override
    public String getPermission() {
        return "customdungeons.party";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            Messages.send(sender, "player-only");
            return;
        }

        if (args.length < 2 || args[1].equalsIgnoreCase("menu")) {
            new PartyMenu(plugin, player).open();
            return;
        }

        String action = args[1].toLowerCase();
        switch (action) {
            case "create": {
                if (plugin.getPartyManager().getParty(player).isPresent()) {
                    Messages.send(player, "party.already-in-party");
                    return;
                }
                plugin.getPartyManager().createParty(player);
                Messages.send(player, "party.created");
                break;
            }
            case "invite": {
                if (args.length < 3) {
                    player.sendMessage(Messages.colorize("&cUsage: /cd party invite <player>"));
                    return;
                }
                Optional<Party> partyOpt = plugin.getPartyManager().getParty(player);
                if (partyOpt.isEmpty()) {
                    Messages.send(player, "party.not-in-party");
                    return;
                }
                Party party = partyOpt.get();
                if (!party.isLeader(player)) {
                    Messages.send(player, "party.not-leader");
                    return;
                }
                Player target = Bukkit.getPlayer(args[2]);
                if (target == null || !target.isOnline()) {
                    Messages.send(player, "party.player-not-found", Map.of("player", args[2]));
                    return;
                }
                if (target.equals(player)) {
                    player.sendMessage(Messages.colorize("&cYou cannot invite yourself to your party."));
                    return;
                }
                if (party.isMember(target)) {
                    player.sendMessage(Messages.colorize("&cThat player is already in your party."));
                    return;
                }
                if (plugin.getPartyManager().getParty(target).isPresent()) {
                    player.sendMessage(Messages.colorize("&cThat player is already in another party."));
                    return;
                }
                party.invite(target);
                Messages.send(player, "party.invite-sent", Map.of("player", target.getName()));
                Messages.send(target, "party.invite-received", Map.of("player", player.getName()));
                break;
            }
            case "accept": {
                if (plugin.getPartyManager().getParty(player).isPresent()) {
                    Messages.send(player, "party.already-in-party");
                    return;
                }
                Optional<Party> partyOpt = plugin.getPartyManager().findPartyWithInvite(player);
                if (partyOpt.isEmpty()) {
                    player.sendMessage(Messages.colorize("&cNo active invitation found."));
                    return;
                }
                Party party = partyOpt.get();
                int maxSize = plugin.getConfig().getInt("party.max-size", 5);
                if (party.getSize() >= maxSize) {
                    Messages.send(player, "party.party-full");
                    return;
                }
                plugin.getPartyManager().addMember(party, player);
                party.broadcast(Messages.get("party.member-joined", Map.of("player", player.getName())));
                break;
            }
            case "kick": {
                if (args.length < 3) {
                    player.sendMessage(Messages.colorize("&cUsage: /cd party kick <player>"));
                    return;
                }
                Optional<Party> partyOpt = plugin.getPartyManager().getParty(player);
                if (partyOpt.isEmpty()) {
                    Messages.send(player, "party.not-in-party");
                    return;
                }
                Party party = partyOpt.get();
                if (!party.isLeader(player)) {
                    Messages.send(player, "party.not-leader");
                    return;
                }
                Player target = Bukkit.getPlayer(args[2]);
                if (target == null || !party.isMember(target)) {
                    player.sendMessage(Messages.colorize("&cThat player is not in your party."));
                    return;
                }
                if (target.equals(player)) {
                    player.sendMessage(Messages.colorize("&cYou cannot kick yourself! Use /cd party leave instead."));
                    return;
                }
                plugin.getPartyManager().removeMember(target);
                party.broadcast(Messages.get("party.member-kicked", Map.of("player", target.getName())));
                Messages.send(target, "party.member-kicked", Map.of("player", target.getName()));
                break;
            }
            case "leave": {
                Optional<Party> partyOpt = plugin.getPartyManager().getParty(player);
                if (partyOpt.isEmpty()) {
                    Messages.send(player, "party.not-in-party");
                    return;
                }
                Party party = partyOpt.get();
                plugin.getQueueManager().dequeue(player, false);
                if (party.isLeader(player)) {
                    plugin.getPartyManager().disbandParty(party);
                    Messages.send(player, "party.disbanded");
                } else {
                    plugin.getPartyManager().removeMember(player);
                    Messages.send(player, "party.member-left", Map.of("player", player.getName()));
                }
                break;
            }
            case "disband": {
                Optional<Party> partyOpt = plugin.getPartyManager().getParty(player);
                if (partyOpt.isEmpty()) {
                    Messages.send(player, "party.not-in-party");
                    return;
                }
                Party party = partyOpt.get();
                if (!party.isLeader(player)) {
                    Messages.send(player, "party.not-leader");
                    return;
                }
                plugin.getQueueManager().dequeue(player, false);
                plugin.getPartyManager().disbandParty(party);
                Messages.send(player, "party.disbanded");
                break;
            }
            default:
                player.sendMessage(Messages.colorize("&cUnknown action. " + getSyntax()));
                break;
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return List.of("create", "invite", "accept", "kick", "leave", "disband", "menu");
        }
        return Collections.emptyList();
    }
}
