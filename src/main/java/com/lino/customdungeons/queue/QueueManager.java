package com.lino.customdungeons.queue;

import com.lino.customdungeons.CustomDungeons;
import com.lino.customdungeons.config.Messages;
import com.lino.customdungeons.core.Dungeon;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class QueueManager {
    private final CustomDungeons plugin;
    private final Map<String, List<QueueEntry>> queues = new HashMap<>();

    public QueueManager(CustomDungeons plugin) {
        this.plugin = plugin;
        startQueueTask();
    }

    public synchronized void enqueue(Player player, String dungeonId) {
        if (isQueued(player)) {
            Messages.send(player, "queue.already-queued");
            return;
        }
        Optional<com.lino.customdungeons.party.Party> partyOpt = plugin.getPartyManager().getParty(player);
        QueueEntry entry = partyOpt.map(party -> new QueueEntry(party, dungeonId)).orElseGet(() -> new QueueEntry(player, dungeonId));

        queues.computeIfAbsent(dungeonId.toLowerCase(), k -> new ArrayList<>()).add(entry);
        Map<String, String> placeholders = Map.of("dungeon", dungeonId);
        for (Player p : entry.getPlayers()) {
            Messages.send(p, "queue.joined", placeholders);
        }
    }

    public synchronized boolean dequeue(Player player) {
        return dequeue(player, true);
    }

    public synchronized boolean dequeue(Player player, boolean notifyIfNotQueued) {
        for (Map.Entry<String, List<QueueEntry>> entry : queues.entrySet()) {
            Iterator<QueueEntry> it = entry.getValue().iterator();
            while (it.hasNext()) {
                QueueEntry qe = it.next();
                if (qe.getLeaderId().equals(player.getUniqueId()) || qe.getPlayers().contains(player)) {
                    it.remove();
                    for (Player p : qe.getPlayers()) {
                        Messages.send(p, "queue.left");
                    }
                    return true;
                }
            }
        }
        if (notifyIfNotQueued) {
            Messages.send(player, "queue.not-queued");
        }
        return false;
    }

    public synchronized boolean isQueued(Player player) {
        for (List<QueueEntry> list : queues.values()) {
            for (QueueEntry entry : list) {
                if (entry.getLeaderId().equals(player.getUniqueId()) || entry.getPlayers().contains(player)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void startQueueTask() {
        Bukkit.getScheduler().runTaskTimer(plugin, this::processQueues, 20L, 20L);
    }

    private synchronized void processQueues() {
        for (Map.Entry<String, List<QueueEntry>> entry : queues.entrySet()) {
            String dungeonId = entry.getKey();
            List<QueueEntry> list = entry.getValue();
            if (list.isEmpty()) continue;

            list.removeIf(qe -> qe.getPlayers().isEmpty());
            if (list.isEmpty()) continue;

            Optional<Dungeon> dungeonOpt = plugin.getDungeonManager().getDungeon(dungeonId);
            if (dungeonOpt.isEmpty()) continue;
            Dungeon dungeon = dungeonOpt.get();

            List<Player> matchedPlayers = new ArrayList<>();
            List<QueueEntry> matchedEntries = new ArrayList<>();

            for (QueueEntry qe : list) {
                List<Player> entryPlayers = qe.getPlayers();
                if (entryPlayers.isEmpty()) continue;
                if (matchedPlayers.size() + entryPlayers.size() <= dungeon.getMaxPlayers()) {
                    matchedPlayers.addAll(entryPlayers);
                    matchedEntries.add(qe);
                }
                if (matchedPlayers.size() >= dungeon.getMinPlayers()) {
                    break;
                }
            }

            if (matchedPlayers.size() >= dungeon.getMinPlayers()) {
                list.removeAll(matchedEntries);
                for (Player p : matchedPlayers) {
                    Messages.send(p, "queue.match-found");
                }
                plugin.getDungeonManager().startInstance(dungeon, matchedPlayers);
            }
        }
    }
}
