package me.deltaorion.townymissionsv2.mission.definition;

import com.palmergames.bukkit.towny.object.TownyObject;
import me.deltaorion.townymissionsv2.mission.MissionGoal;

import java.util.Map;

//for all mission definitions
public interface GoalDefinition {

    public String getName();

    public void onLoad();

    public void onDisable();


}
