package com.ceedric.event.eventmobs.model.boss;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.Location;

public class BossStart {

    private final String bossName;
    private final Location spawnLocation;
    private boolean alive = true;

    public BossStart(String bossName, Location spawnLocation) {
        this.bossName = bossName;
        this.spawnLocation = spawnLocation;
        this.alive = false;
    }

    public String getBossName() {
        return bossName;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public MythicMob getBossMob() {
        return MythicBukkit.inst().getMobManager().getMythicMob(bossName).orElse(null);
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }
}
