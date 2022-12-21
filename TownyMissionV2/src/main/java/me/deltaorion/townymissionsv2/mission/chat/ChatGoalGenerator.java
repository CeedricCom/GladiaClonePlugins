package me.deltaorion.townymissionsv2.mission.chat;

import me.deltaorion.townymissionsv2.Definition;
import me.deltaorion.townymissionsv2.TownyMissionsV2;
import me.deltaorion.townymissionsv2.bearer.MissionBearer;
import me.deltaorion.townymissionsv2.configuration.ConfigurationException;
import me.deltaorion.townymissionsv2.mission.MissionGoal;
import me.deltaorion.townymissionsv2.mission.definition.GoalDefinition;
import me.deltaorion.townymissionsv2.mission.goal.CompletableGoalGenerator;
import me.deltaorion.townymissionsv2.mission.reward.GoalReward;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class ChatGoalGenerator extends CompletableGoalGenerator {

    private final String word;
    protected ChatGoalGenerator(GoalDefinition definition, int goal, List<GoalReward> rewards, String word) {
        super(definition, goal, rewards);
        this.word = word;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String,Object> begin = super.beginSerialize();
        begin.put("word",word);
        return begin;
    }

    public static ChatGoalGenerator deserialize(ConfigurationSection section, TownyMissionsV2 plugin) {
        GoalDefinition definition = plugin.getDefinition(Definition.CHAT.getName());
        int goal = getGoal(section);
        List<GoalReward> rewards = getRewards(section);

        if(!section.contains("word"))
            throw new ConfigurationException(section,"word");

        String word = section.getString("word");

        return new ChatGoalGenerator(definition,goal,rewards,word);
    }

    @Override
    public MissionGoal generateGoal(MissionBearer bearer) {
        return new ChatGoal(getDefinition(),getGoal(),bearer,getRewards(),word);
    }

    public String toString() {
        return super.toString() + " Word: "+word;
    }
}
