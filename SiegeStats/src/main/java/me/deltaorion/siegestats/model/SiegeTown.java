package me.deltaorion.siegestats.model;

import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Town;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class SiegeTown {

    private final UUID uniqueId;
    private final List<StatSiege> sieges;
    private String name;

    public SiegeTown(UUID uniqueId, String name) {
        this.uniqueId = uniqueId;
        this.sieges = new ArrayList<>();
        this.name = name;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public List<StatSiege> getSieges() {
        return Collections.unmodifiableList(sieges);
    }

    @Nullable
    public StatSiege getLatestSiege() {
        if(sieges.size()==0)
            return null;

        return sieges.get(sieges.size()-1);
    }

    public String getLastName() {
        return name;
    }

    public Town asTown() {
        return TownyUniverse.getInstance().getTown(uniqueId);
    }


    public void setName(String name) {
        this.name = name;
    }

    public void addSiege(StatSiege statSiege) {
        this.sieges.add(statSiege);
    }
}
