package com.ceedric.event.eventmobs.config;

import com.ceedric.event.eventmobs.model.MythicBoss;
import com.ceedric.event.eventmobs.model.participant.BossSide;
import io.lumine.mythic.api.mobs.MythicMob;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.List;
import java.util.Map;

public interface MythicConfig {

    List<MythicBoss> getBosses();

    Location getSpawnLocation();

    MythicMob getBoss();

    World getWorld();

    void reload();

    Map<BossSide,String> getNames();
}
