package me.deltaorion.townymissionsv2.mission;

import me.deltaorion.townymissionsv2.Definition;
import me.deltaorion.townymissionsv2.TownyMissionsV2;
import me.deltaorion.townymissionsv2.bearer.MissionBearer;
import me.deltaorion.townymissionsv2.configuration.ConfigurationException;
import me.deltaorion.townymissionsv2.mission.reward.MissionReward;
import me.deltaorion.townymissionsv2.player.ContributeType;
import me.deltaorion.townymissionsv2.util.DurationParser;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.*;

public class MissionGenerator implements ConfigurationSerializable {

    private final List<GoalGenerator> goals;
    private final List<MissionReward> rewards;
    private final Duration duration;
    private final String name;
    private final ContributeType type;

    public MissionGenerator(String name, List<GoalGenerator> goals, List<MissionReward> rewards, Duration duration, ContributeType type) {
        this.goals = goals;
        this.rewards = rewards;
        this.duration = duration;
        this.name = name;
        this.type = type;
    }

    public MissionGenerator(String name, List<GoalGenerator> goals, List<MissionReward> rewards, ContributeType type) {
        this(name,goals,rewards,Mission.INFINITE_DURATION, type);
    }

    public Mission getMission(MissionBearer bearer) {
        Mission.Builder builder = new Mission.Builder(bearer,duration);
        for(GoalGenerator goal : goals) {
            builder.appendGoal(goal.generateGoal(bearer));
        }
        for(MissionReward reward : rewards) {
            builder.addReward(reward.clone());
        }
        return builder.build();
    }

    public ContributeType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public static MissionGenerator deserialize(@NotNull ConfigurationSection section, TownyMissionsV2 plugin) {

        /**
         * name: 'mission-name'
         * duration: '7d'
         * rewards:
         *   1:
         *      type: 'command'
         *      command: 'give %s stone %amount%'
         *      name: 'stone'
         *      total: 5
         * goals:
         *
         *
         *
         */

        if(!section.contains("name"))
            throw new ConfigurationException(section,"name");

        String name = section.getString("name");

        Duration duration = null;

        if(section.contains("duration")) {
            try {
                duration = DurationParser.parseDuration(section.getString("duration"));
            } catch (IllegalArgumentException e) {
                throw new ConfigurationException("duration",section.get("duration"),"Unable to read duration");
            }
        } else {
            duration = Mission.INFINITE_DURATION;
        }

        List<MissionReward> rewards = new ArrayList<>();

        if (section.contains("rewards")) {
            ConfigurationSection rewardSection = section.getConfigurationSection("rewards");
            for(String key : rewardSection.getKeys(false)) {
                MissionReward reward = MissionReward.deserialize(rewardSection.getConfigurationSection(key));
                rewards.add(reward);
            }
        }
        List<GoalGenerator> goals = new ArrayList<>();
        if(section.contains("goals")) {
            ConfigurationSection goalSection = section.getConfigurationSection("goals");
            for(String key : goalSection.getKeys(false)) {
                ConfigurationSection goal = goalSection.getConfigurationSection(key);
                GoalGenerator generator = Definition.deserialize(goal,plugin);
                goals.add(generator);
            }
        } else {
            throw new ConfigurationException(section,"goals");
        }

        ContributeType type = null;
        if(section.contains("type")) {
            try {
                type = ContributeType.valueOf(section.getString("type").toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ConfigurationException("type", section.getString("type"), "Unknown Mission Bearer Type");
            }
        }

        return new MissionGenerator(name,goals,rewards,duration, type);
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String,Object> map = new LinkedHashMap<>();
        map.put("name",name);
        if(!duration.equals(Mission.INFINITE_DURATION)) {
            map.put("duration",DurationParser.print(duration));
        }
        Map<String,Object> rewardsMap = new LinkedHashMap<>();
        for(int i=0;i<rewards.size();i++) {
            rewardsMap.put(String.valueOf(i),rewards.get(i).serialize());
        }
        map.put("rewards",rewardsMap);
        Map<String,Object> goalMap = new LinkedHashMap<>();
        for(int i=0;i<goals.size();i++) {
            goalMap.put(String.valueOf(i),goals.get(i).serialize());
        }
        map.put("goals",goalMap);

        return map;
    }

    public boolean isOfType(ContributeType type) {
        return Objects.equals(this.type,type);
    }

    public String toString() {
        return "Name: "+name + " Duration: "+DurationParser.print(duration) + " Type: "+type+" Rewards: "+rewards + " Goals: "+goals;
    }
}
