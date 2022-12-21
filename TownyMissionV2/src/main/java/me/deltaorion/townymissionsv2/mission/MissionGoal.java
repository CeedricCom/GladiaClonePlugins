package me.deltaorion.townymissionsv2.mission;

import com.palmergames.bukkit.towny.object.TownyObject;
import me.deltaorion.townymissionsv2.mission.definition.GoalDefinition;
import me.deltaorion.townymissionsv2.mission.reward.Rewardable;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.Map;
import java.util.UUID;

//missions consist of many goals,
//missions have rewards given to the player
public interface MissionGoal extends Rewardable {

    public String getName();

    public int contribute(UUID user, int quantity);

    public boolean isComplete();

    public Mission getMission();

    public void setMission(Mission mission);

    public GoalDefinition getDefinition();

    public void complete();

    public void forceComplete();

    public String getDisplayText();

    public int getProgress();

    public void start();
}
