package me.deltaorion.townymissionsv2.mission.gather;

import com.google.common.base.Preconditions;
import me.deltaorion.townymissionsv2.Definition;
import me.deltaorion.townymissionsv2.TownyMissionsV2;
import me.deltaorion.townymissionsv2.bearer.MissionBearer;
import me.deltaorion.townymissionsv2.configuration.ConfigurationException;
import me.deltaorion.townymissionsv2.mission.goal.CompletableGoalGenerator;
import me.deltaorion.townymissionsv2.mission.reward.GoalReward;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class GatherGoalGenerator extends CompletableGoalGenerator {

    private final Material material;

    public GatherGoalGenerator(Material material, int goal, List<GoalReward> rewards, TownyMissionsV2 plugin) {
        super(plugin.getDefinition(Definition.GATHER.getName()),goal,rewards);

        Preconditions.checkNotNull(material);
        Preconditions.checkNotNull(rewards);

        if(goal<0)
            throw new IllegalArgumentException("Goal must be above 0");

        this.material = material;
    }

    @Override
    public GatherGoal generateGoal(MissionBearer bearer) {
        return new GatherGoal(getDefinition(),bearer,getGoal(),material,getRewards());
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String,Object> begin = super.beginSerialize();
        begin.put("material",material.toString());

        return begin;
    }

    public static GatherGoalGenerator deserialize(ConfigurationSection section, TownyMissionsV2 plugin) {

        int goal = getGoal(section);
        List<GoalReward> rewards = getRewards(section);

        if(!section.contains("material"))
            throw new ConfigurationException(section,"material");

        Material material = null;
        try {
            material = Material.valueOf(section.getString("material"));
        } catch (IllegalArgumentException e) {
            throw new ConfigurationException("material",section.getString("material"),"Unknown Material");
        }

        return new GatherGoalGenerator(material,goal,rewards,plugin);
    }

    public String toString() {
        return super.toString() + " material: "+material;
    }
}
