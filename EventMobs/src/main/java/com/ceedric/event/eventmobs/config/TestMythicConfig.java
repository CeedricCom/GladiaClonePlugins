package com.ceedric.event.eventmobs.config;

import com.ceedric.event.eventmobs.model.MythicBoss;
import com.ceedric.event.eventmobs.model.participant.BossSide;
import io.lumine.mythic.api.mobs.MythicMob;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TestMythicConfig implements MythicConfig {

    private final List<MythicBoss> bosses;
    private final Location spawnLocation;
    private final World world;
    private final MythicMob boss;
    private final Map<BossSide,String> names;

    public TestMythicConfig(Location spawnLocation, World world, MythicMob boss, Map<BossSide, String> names) {
        this.spawnLocation = spawnLocation;
        this.world = world;
        this.boss = boss;
        this.names = names;
        bosses = new ArrayList<>();
    }

    @Override
    public List<MythicBoss> getBosses() {
        return bosses;
    }

    public void addBoss(MythicBoss boss) {
        this.bosses.add(boss);
    }

    @Override
    public Location getSpawnLocation() {
        return spawnLocation;
    }

    @Override
    public MythicMob getBoss() {
        return boss;
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public void reload() {

    }

    @Override
    public Map<BossSide, String> getNames() {
        return names;
    }
}
