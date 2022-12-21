package me.deltaorion.townymissionsv2.mission.goal;

import com.google.common.base.Preconditions;
import me.deltaorion.townymissionsv2.Definition;
import me.deltaorion.townymissionsv2.bearer.MissionBearer;
import me.deltaorion.townymissionsv2.configuration.Message;
import me.deltaorion.townymissionsv2.display.MissionSound;
import me.deltaorion.townymissionsv2.mission.Mission;
import me.deltaorion.townymissionsv2.mission.MissionGoal;
import me.deltaorion.townymissionsv2.mission.definition.GoalDefinition;
import org.bukkit.ChatColor;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class CompletableGoal implements MissionGoal {

    private final int goal;
    private int progress;
    private final MissionBearer master;
    private Mission mission;
    private final GoalDefinition definition;

    public CompletableGoal(int goal, MissionBearer master, GoalDefinition definition) {

        Preconditions.checkNotNull(definition);
        Preconditions.checkNotNull(master);

        if(goal < 0)
            throw new IllegalArgumentException("Mission Goal must be positive");

        this.goal = goal;
        this.definition = definition;
        this.master = master;
    }

    protected CompletableGoal(MissionBearer master, Mission mission ,GoalDefinition definition, int goal, int progress) {
        this.master = master;
        this.definition = definition;
        this.mission = mission;
        this.goal = goal;
        this.progress = progress;
    }

    @Override
    public boolean isComplete() {
        return progress >= goal;
    }

    protected int getGoal() {
        return goal;
    }

    @Override
    public int getProgress() {
        return progress;
    }

    protected void setProgress(int progress) {
        this.progress = progress;
    }

    protected MissionBearer getMaster() {
        return master;
    }

    public void complete() {

        displayCompletion();
        distributeRewards();

        if(this.mission!=null) {
            mission.nextStage();
        }

    }

    private void displayCompletion() {
        getMaster().sendMessage(Message.MISSION_GOAL_COMPLETION.getMessage(ChatColor.stripColor(getDisplayText())));
        getMaster().playSound(MissionSound.MISSION_GOAL_COMPLETION.getSound());
        getMaster().sendTitle(Message.MISSION_GOAL_COMPLETE_TITLE.getMessage(),Message.MISSION_GOAL_COMPLETE_SUBTITLE.getMessage(ChatColor.stripColor(getDisplayText())));
    }

    @Override
    public Mission getMission() {
        return this.mission;
    }

    @Override
    public void setMission(Mission mission) {
        this.mission = mission;
    }

    @Override
    public GoalDefinition getDefinition() {
        return definition;
    }

    public void loadParameters(PreparedStatement sup, int stage) throws SQLException {
        sup.setString(1,getMission().getUniqueID().toString());
        sup.setInt(2,stage);
        sup.setInt(3,getGoal());
        sup.setString(4, Definition.fromClass(getDefinition()).toString());
        sup.setInt(5,getProgress());
        sup.addBatch();
    }
}
