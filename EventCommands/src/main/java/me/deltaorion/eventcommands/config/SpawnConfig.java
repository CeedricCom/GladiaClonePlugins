package me.deltaorion.eventcommands.config;

import me.deltaorion.eventcommands.EventSpawn;
import me.deltaorion.eventcommands.SpawnTemplate;

import java.util.Collection;

public interface SpawnConfig {

    Collection<SpawnTemplate> getSpawns();

    void reload();

}
