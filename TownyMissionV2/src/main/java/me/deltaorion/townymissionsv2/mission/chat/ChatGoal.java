package me.deltaorion.townymissionsv2.mission.chat;

import com.google.common.base.Preconditions;
import me.deltaorion.townymissionsv2.Definition;
import me.deltaorion.townymissionsv2.TownyMissionsV2;
import me.deltaorion.townymissionsv2.bearer.MissionBearer;
import me.deltaorion.townymissionsv2.configuration.Message;
import me.deltaorion.townymissionsv2.mission.Mission;
import me.deltaorion.townymissionsv2.mission.MissionGoal;
import me.deltaorion.townymissionsv2.mission.definition.GoalDefinition;
import me.deltaorion.townymissionsv2.mission.goal.CollectiveGoal;
import me.deltaorion.townymissionsv2.mission.reward.GoalReward;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ChatGoal extends CollectiveGoal {

    private final String word;

    public ChatGoal(GoalDefinition definition, int goal, MissionBearer master, List<GoalReward> rewards, String word) {
        super(goal, master, definition, rewards);
        this.word = word;
    }

    public ChatGoal(GoalDefinition definition, int goal, MissionBearer master, String word) {
        super(goal, master, definition);
        this.word = word;
    }

    protected ChatGoal(GoalDefinition definition, MissionBearer master, Mission mission, int progress, int goal, Map<UUID,Integer> contributions, String word, List<GoalReward> rewards) {
        super(master,mission,definition,progress,goal,contributions, rewards);
        this.word = word;
    }

    public static MissionGoal fromSave(TownyMissionsV2 plugin, Definition definition, int progress, int amount, String word, Map<UUID,Integer> contributions, Mission mission, List<GoalReward> rewards) {
        Preconditions.checkNotNull(word);
        Preconditions.checkNotNull(definition);
        Preconditions.checkNotNull(contributions);

        GoalDefinition goalDefinition = plugin.getDefinition(definition);
        return new ChatGoal(goalDefinition, mission.getMissionBearer(), mission,progress,amount,contributions,word,rewards);
    }

    @Override
    public String getName() {
        return "Chat";
    }

    @Override
    public String getDisplayText() {
        return Message.GOV_CHAT_GOAL_TEXT.getMessage(word,getGoal(),getProgress());
    }

    public String getWord() {
        return word;
    }

    public String toString() {
        return "Chat Goal - Word: "+word + " Progress: "+getProgress()+" Goal: "+getGoal();
    }

    public void loadParameters(PreparedStatement sup, PreparedStatement sub, PreparedStatement contributions, int stage) throws SQLException {
        loadParameters(sup,stage);
        saveContributions(contributions,stage);

        sub.setInt(1,stage);
        sub.setString(2,getMission().getUniqueID().toString());
        sub.setString(3,word);

        sub.addBatch();
    }
}
