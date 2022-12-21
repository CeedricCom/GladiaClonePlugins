package me.deltaorion.townymissionsv2.mission.chat;

import me.deltaorion.townymissionsv2.TownyMissionsV2;
import me.deltaorion.townymissionsv2.bearer.MissionBearer;
import me.deltaorion.townymissionsv2.mission.MissionGoal;
import me.deltaorion.townymissionsv2.mission.definition.AbstractDefinition;
import me.deltaorion.townymissionsv2.mission.reward.GoalReward;

import java.util.Arrays;

public class ChatDefinition extends AbstractDefinition {

    public ChatDefinition(TownyMissionsV2 plugin) {
        super(plugin, "Chat");
    }


    @Override
    public void onLoad() {
        getPlugin().getServer().getPluginManager().registerEvents(new ChatListener(getPlugin(),this),getPlugin());
    }

    @Override
    public void onDisable() {

    }


    public MissionGoal generateGoal(MissionBearer government, String word, int goal, GoalReward... rewards) {
        return new ChatGoal(this,goal,government,Arrays.asList(rewards),word);
    }



}
