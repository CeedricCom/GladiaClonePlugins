package me.deltaorion.towntier.towntiers.townyutils;

import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.Location;
import java.util.HashMap;

public class TownSpawn {
    private Town town;
    private HashMap<Integer,Location> spawns;

    public TownSpawn(Town town) {
        this.town = town;
        spawns = new HashMap<>();
    }

    public Town getTown() {
        return town;
    }

    public void setTown(Town town) {
        this.town = town;
    }

    public HashMap<Integer, Location> getSpawns() {
        return spawns;
    }

    public void setSpawns(HashMap<Integer, Location> spawns) {
        this.spawns = spawns;
    }
}
