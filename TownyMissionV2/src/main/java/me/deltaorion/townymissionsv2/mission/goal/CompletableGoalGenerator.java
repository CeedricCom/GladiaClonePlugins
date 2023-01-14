package me.deltaorion.townymissionsv2.mission.goal;

import me.deltaorion.townymissionsv2.Definition;
import me.deltaorion.townymissionsv2.configuration.ConfigurationException;
import me.deltaorion.townymissionsv2.mission.GoalGenerator;
import me.deltaorion.townymissionsv2.mission.definition.GoalDefinition;
import me.deltaorion.townymissionsv2.mission.reward.GoalReward;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class CompletableGoalGenerator implements GoalGenerator {

    private final GoalDefinition definition;
    private final int goal;
    private final List<GoalReward> rewards;

    protected CompletableGoalGenerator(GoalDefinition definition, int goal, List<GoalReward> rewards) {
        this.definition = definition;
        this.goal = goal;
        this.rewards = rewards;
    }

    public @NotNull Map<String, Object> beginSerialize() {
        /**
         * definition: gather
         * goal: goal
         * material: material
         * rewards:
         *  1:
         *      abc
         *
         */
        Map<String,Object> map = new LinkedHashMap<>();
        map.put("definition",Definition.fromClass(definition).toString());
        map.put("goal",goal);
        Map<String,Object> rewardsMap = new LinkedHashMap<>();
        for(int i=0;i<rewards.size();i++) {
            GoalReward goalReward = rewards.get(i);
            rewardsMap.put(String.valueOf(i),goalReward.serialize());
        }
        map.put("rewards",rewardsMap);
        return map;
    }

    protected static int getGoal(ConfigurationSection section) {
        if(!section.contains("goal"))
            throw new ConfigurationException(section,"goal");

        return section.getInt("goal");
    }

    protected static List<GoalReward> getRewards(ConfigurationSection section) {
        List<GoalReward> rewards = new ArrayList<>();
        if(section.contains("rewards")) {
            ConfigurationSection rewardSection = section.getConfigurationSection("rewards");
            for(String key : rewardSection.getKeys(false)) {
                GoalReward goalReward = GoalReward.deserialize(rewardSection.getConfigurationSection(key));
                rewards.add(goalReward);
            }
        }

        return rewards;
    }




    protected GoalDefinition getDefinition() {
        return definition;
    }

    public int getGoal() {
        return goal;
    }

    public List<GoalReward> getRewards() {
        List<GoalReward> generated = new ArrayList<>();
        for(GoalReward reward : rewards) {
            generated.add(reward.clone());
        }
        return generated;
    }

    public String toString() {
        return "Definition: "+Definition.fromClass(definition) + " Goal: "+goal + " Rewards: "+rewards;
    }


}
