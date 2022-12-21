package me.deltaorion.townymissionsv2;

import me.deltaorion.townymissionsv2.mission.GoalGenerator;
import me.deltaorion.townymissionsv2.mission.chat.ChatDefinition;
import me.deltaorion.townymissionsv2.mission.chat.ChatGoalGenerator;
import me.deltaorion.townymissionsv2.mission.definition.GoalDefinition;
import me.deltaorion.townymissionsv2.mission.gather.GatherDefinition;
import me.deltaorion.townymissionsv2.mission.gather.GatherGoalGenerator;
import org.bukkit.configuration.ConfigurationSection;

public enum Definition {

    CHAT("Chat"),
    GATHER("Gather");

    private final String name;

    Definition(String name) {
        this.name = name;
    }

    public static Definition fromClass(GoalDefinition definition) {
        if(definition instanceof GatherDefinition)
            return Definition.GATHER;

        if(definition instanceof ChatDefinition)
            return Definition.CHAT;

        throw new IllegalStateException("Definition '"+definition+"' is not properly linked to the definition enum");
    }

    public static GoalGenerator deserialize(ConfigurationSection section, TownyMissionsV2 plugin) {
        Definition definition = GoalGenerator.getDefinition(section);
        switch (definition) {
            case GATHER:
                return GatherGoalGenerator.deserialize(section,plugin);
            case CHAT:
                return ChatGoalGenerator.deserialize(section,plugin);
        }
        throw new IllegalStateException("Goal Generator not properly linked to definition enum '"+definition+"'");
    }

    public String getName() {
        return name;
    }
}
