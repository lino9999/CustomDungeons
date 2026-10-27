package com.lino.customdungeons.gui;

import com.lino.customdungeons.CustomDungeons;
import com.lino.customdungeons.config.Messages;
import com.lino.customdungeons.party.Party;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PartyMenu extends Menu {
    private final CustomDungeons plugin;

    public PartyMenu(CustomDungeons plugin, Player player) {
        super(player, "&8&lYour Dungeon Party", 36);
        this.plugin = plugin;
    }

    @Override
    public void build() {
        Optional<Party> partyOpt = plugin.getPartyManager().getParty(player);
        if (partyOpt.isEmpty()) {
            ItemStack createItem = new ItemStack(Material.EMERALD);
            ItemMeta meta = createItem.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(Messages.colorize("&a&lCreate Party"));
                meta.setLore(List.of(Messages.colorize("&7Click to form a new dungeon party.")));
                createItem.setItemMeta(meta);
            }
            setItem(13, new MenuItem(createItem, event -> {
                plugin.getPartyManager().createParty(player);
                Messages.send(player, "party.created");
                new PartyMenu(plugin, player).open();
            }));
            return;
        }

        Party party = partyOpt.get();
        int slot = 10;
        for (UUID memberId : party.getMembers()) {
            Player member = Bukkit.getPlayer(memberId);
            String name = member != null ? member.getName() : "Offline Member";
            boolean isLeader = party.isLeader(member != null ? member : player);

            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            if (meta != null) {
                if (member != null) meta.setOwningPlayer(member);
                meta.setDisplayName(Messages.colorize((isLeader ? "&6&l[Leader] &e" : "&a&l[Member] &e") + name));
                skull.setItemMeta(meta);
            }
            setItem(slot++, new MenuItem(skull));
        }

        ItemStack leaveItem = new ItemStack(Material.REDSTONE);
        ItemMeta leaveMeta = leaveItem.getItemMeta();
        if (leaveMeta != null) {
            leaveMeta.setDisplayName(Messages.colorize(party.isLeader(player) ? "&c&lDisband Party" : "&c&lLeave Party"));
            leaveItem.setItemMeta(leaveMeta);
        }
        setItem(31, new MenuItem(leaveItem, event -> {
            player.closeInventory();
            if (party.isLeader(player)) {
                plugin.getPartyManager().disbandParty(party);
                Messages.send(player, "party.disbanded");
            } else {
                plugin.getPartyManager().removeMember(player);
                Messages.send(player, "party.member-left", java.util.Map.of("player", player.getName()));
            }
        }));
    }
}
