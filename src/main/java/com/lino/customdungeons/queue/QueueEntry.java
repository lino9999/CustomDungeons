package com.lino.customdungeons.queue;

import com.lino.customdungeons.party.Party;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class QueueEntry {
    private final UUID leaderId;
    private final String dungeonId;
    private final Party party;
    private final long joinTime;

    public QueueEntry(Player player, String dungeonId) {
        this.leaderId = player.getUniqueId();
        this.dungeonId = dungeonId;
        this.party = null;
        this.joinTime = System.currentTimeMillis();
    }

    public QueueEntry(Party party, String dungeonId) {
        this.leaderId = party.getLeader();
        this.dungeonId = dungeonId;
        this.party = party;
        this.joinTime = System.currentTimeMillis();
    }

    public UUID getLeaderId() {
        return leaderId;
    }

    public String getDungeonId() {
        return dungeonId;
    }

    public Party getParty() {
        return party;
    }

    public List<Player> getPlayers() {
        if (party != null) {
            return party.getOnlinePlayers();
        }
        Player leader = Bukkit.getPlayer(leaderId);
        return (leader != null && leader.isOnline()) ? Collections.singletonList(leader) : Collections.emptyList();
    }

    public int getPlayerCount() {
        return getPlayers().size();
    }

    public long getJoinTime() {
        return joinTime;
    }
}
