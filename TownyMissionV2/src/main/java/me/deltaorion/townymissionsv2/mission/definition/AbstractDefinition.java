package me.deltaorion.townymissionsv2.mission.definition;

import me.deltaorion.townymissionsv2.TownyMissionsV2;
import me.deltaorion.townymissionsv2.mission.Mission;

public abstract class AbstractDefinition implements GoalDefinition{

    private final TownyMissionsV2 plugin;
    private final String name;

    public AbstractDefinition(TownyMissionsV2 plugin, String name) {
        this.plugin = plugin;
        this.name = name;
    }

    public TownyMissionsV2 getPlugin() {
        return plugin;
    }

    protected void giveMission(Mission mission) {
        plugin.getMissionManager().registerMission(mission);
    }

    @Override
    public String getName() {
        return name;
    }
}
