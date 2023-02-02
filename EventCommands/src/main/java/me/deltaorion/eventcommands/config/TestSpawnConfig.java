package me.deltaorion.eventcommands.config;

import me.deltaorion.eventcommands.EventSpawn;
import me.deltaorion.eventcommands.SpawnTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TestSpawnConfig implements SpawnConfig {

    private final List<SpawnTemplate> spawns;

    public TestSpawnConfig() {
        spawns = new ArrayList<>();
    }

    @Override
    public Collection<SpawnTemplate> getSpawns() {
        return spawns;
    }

    @Override
    public void reload() {

    }

    public void addSpawn(SpawnTemplate spawn) {
        this.spawns.add(spawn);
    }
}
