package com.lino.customdungeons.party;

import com.lino.customdungeons.CustomDungeons;
import org.bukkit.entity.Player;

import java.util.*;

public class PartyManager {
    private final CustomDungeons plugin;
    private final Map<UUID, Party> partiesByMember = new HashMap<>();
    private final Map<UUID, Party> activeParties = new HashMap<>();

    public PartyManager(CustomDungeons plugin) {
        this.plugin = plugin;
    }

    public Party createParty(Player leader) {
        if (partiesByMember.containsKey(leader.getUniqueId())) {
            return partiesByMember.get(leader.getUniqueId());
        }
        Party party = new Party(leader);
        activeParties.put(party.getId(), party);
        partiesByMember.put(leader.getUniqueId(), party);
        return party;
    }

    public Optional<Party> getParty(Player player) {
        return Optional.ofNullable(partiesByMember.get(player.getUniqueId()));
    }

    public void addMember(Party party, Player player) {
        party.addMember(player);
        partiesByMember.put(player.getUniqueId(), party);
    }

    public void removeMember(Player player) {
        Party party = partiesByMember.remove(player.getUniqueId());
        if (party != null) {
            party.removeMember(player);
            if (party.getMembers().isEmpty()) {
                activeParties.remove(party.getId());
            }
        }
    }

    public void disbandParty(Party party) {
        for (UUID member : party.getMembers()) {
            partiesByMember.remove(member);
        }
        activeParties.remove(party.getId());
    }

    public Optional<Party> findPartyWithInvite(Player invitee) {
        long timeout = plugin.getConfig().getLong("party.invite-timeout-seconds", 60) * 1000L;
        for (Party party : activeParties.values()) {
            if (party.hasPendingInvite(invitee, timeout)) {
                return Optional.of(party);
            }
        }
        return Optional.empty();
    }
}
