package me.deltaorion.townymissionsv2.mission;

import me.deltaorion.townymissionsv2.Definition;
import me.deltaorion.townymissionsv2.TownyMissionsV2;
import me.deltaorion.townymissionsv2.bearer.MissionBearer;
import me.deltaorion.townymissionsv2.configuration.ConfigurationException;
import me.deltaorion.townymissionsv2.mission.definition.GoalDefinition;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public interface GoalGenerator extends ConfigurationSerializable {

    public MissionGoal generateGoal(MissionBearer bearer);

    public static Definition getDefinition(ConfigurationSection section) {

        if(!section.contains("definition"))
            throw new ConfigurationException(section,"definition");

        Definition definition = null;
        try {
            definition = Definition.valueOf(section.getString("definition").toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ConfigurationException("definition",section.getString("definition"),"Unknown Goal Definition");
        }

        return definition;
    }
}
