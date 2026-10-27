package com.lino.customdungeons.api;

import com.lino.customdungeons.CustomDungeons;
import com.lino.customdungeons.core.Dungeon;
import com.lino.customdungeons.core.DungeonInstance;
import com.lino.customdungeons.party.Party;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Optional;

public class CustomDungeonsAPI {
    private final CustomDungeons plugin;

    public CustomDungeonsAPI(CustomDungeons plugin) {
        this.plugin = plugin;
    }

    public Optional<DungeonInstance> getActiveInstance(Player player) {
        return plugin.getDungeonManager().getInstance(player);
    }

    public boolean isInDungeon(Player player) {
        return plugin.getDungeonManager().isInDungeon(player);
    }

    public Optional<Party> getParty(Player player) {
        return plugin.getPartyManager().getParty(player);
    }

    public Optional<Dungeon> getDungeon(String id) {
        return plugin.getDungeonManager().getDungeon(id);
    }

    public Collection<Dungeon> getLoadedDungeons() {
        return plugin.getDungeonManager().getDungeons();
    }
}
