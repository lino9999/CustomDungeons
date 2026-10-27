package com.lino.customdungeons.party;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class Party {
    private final UUID id;
    private UUID leader;
    private final Set<UUID> members = new HashSet<>();
    private final Map<UUID, Long> pendingInvites = new HashMap<>();

    public Party(Player leader) {
        this.id = UUID.randomUUID();
        this.leader = leader.getUniqueId();
        this.members.add(leader.getUniqueId());
    }

    public UUID getId() {
        return id;
    }

    public UUID getLeader() {
        return leader;
    }

    public void setLeader(UUID leader) {
        this.leader = leader;
    }

    public Set<UUID> getMembers() {
        return Collections.unmodifiableSet(members);
    }

    public boolean isLeader(Player player) {
        return leader.equals(player.getUniqueId());
    }

    public boolean isMember(Player player) {
        return members.contains(player.getUniqueId());
    }

    public void addMember(Player player) {
        members.add(player.getUniqueId());
        pendingInvites.remove(player.getUniqueId());
    }

    public void removeMember(Player player) {
        members.remove(player.getUniqueId());
        if (leader.equals(player.getUniqueId()) && !members.isEmpty()) {
            this.leader = members.iterator().next();
        }
    }

    public void invite(Player player) {
        pendingInvites.put(player.getUniqueId(), System.currentTimeMillis());
    }

    public boolean hasPendingInvite(Player player, long timeoutMillis) {
        Long time = pendingInvites.get(player.getUniqueId());
        if (time == null) return false;
        if (System.currentTimeMillis() - time > timeoutMillis) {
            pendingInvites.remove(player.getUniqueId());
            return false;
        }
        return true;
    }

    public int getSize() {
        return members.size();
    }

    public List<Player> getOnlinePlayers() {
        List<Player> list = new ArrayList<>();
        for (UUID uuid : members) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null && p.isOnline()) {
                list.add(p);
            }
        }
        return list;
    }

    public void broadcast(String message) {
        for (Player p : getOnlinePlayers()) {
            p.sendMessage(message);
        }
    }
}
