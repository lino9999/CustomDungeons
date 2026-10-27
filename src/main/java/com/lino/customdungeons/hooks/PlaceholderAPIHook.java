package com.lino.customdungeons.hooks;

import com.lino.customdungeons.CustomDungeons;
import com.lino.customdungeons.core.DungeonInstance;
import com.lino.customdungeons.party.Party;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;

public class PlaceholderAPIHook extends PlaceholderExpansion {
    private final CustomDungeons plugin;

    public PlaceholderAPIHook(CustomDungeons plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "customdungeons";
    }

    @Override
    public String getAuthor() {
        return "Lino";
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        if (player == null) return "";

        Optional<DungeonInstance> instOpt = plugin.getDungeonManager().getInstance(player);
        Optional<Party> partyOpt = plugin.getPartyManager().getParty(player);

        switch (params.toLowerCase()) {
            case "in_dungeon":
                return instOpt.isPresent() ? "true" : "false";
            case "current":
                return instOpt.map(instance -> instance.getDungeon().getDisplayName()).orElse("None");
            case "stage":
                return instOpt.map(instance -> String.valueOf(instance.getCurrentStageIndex())).orElse("0");
            case "lives":
                return instOpt.map(instance -> String.valueOf(instance.getLives(player))).orElse("0");
            case "time":
                if (instOpt.isEmpty()) return "00:00";
                int secs = instOpt.get().getSecondsRemaining();
                return String.format("%02d:%02d", secs / 60, secs % 60);
            case "party_size":
                return partyOpt.map(p -> String.valueOf(p.getSize())).orElse("0");
            case "party_leader":
                if (partyOpt.isEmpty()) return "None";
                Player leader = Bukkit.getPlayer(partyOpt.get().getLeader());
                return leader != null ? leader.getName() : "Unknown";
            default:
                return null;
        }
    }
}
