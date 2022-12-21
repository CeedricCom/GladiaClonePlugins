package me.deltaorion.townymissionsv2.mission;

import com.google.common.base.Preconditions;
import me.deltaorion.townymissionsv2.TownyMissionsV2;
import me.deltaorion.townymissionsv2.bearer.MissionBearer;
import me.deltaorion.townymissionsv2.display.MissionSound;
import me.deltaorion.townymissionsv2.mission.reward.AbstractReward;
import me.deltaorion.townymissionsv2.mission.reward.MissionReward;
import me.deltaorion.townymissionsv2.mission.reward.Rewardable;
import me.deltaorion.townymissionsv2.storage.Saveable;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Mission implements Rewardable, Saveable {

    public static final Duration INFINITE_DURATION = Duration.of(-1, ChronoUnit.DAYS);

    private final MissionBearer bearer;
    private final List<MissionGoal> goals;
    private final List<MissionReward> rewards;
    private final Duration duration;
    private final long startTime;
    private int stage;
    private final UUID uniqueID;

    public Mission(Builder builder) {
        this.stage = 0;
        this.duration = builder.duration;
        this.rewards = builder.rewards;
        this.goals = builder.goals;
        this.bearer = builder.bearer;
        this.startTime = System.currentTimeMillis();
        this.uniqueID = UUID.randomUUID();

        init();
    }

    private Mission(MissionBearer bearer, UUID UUID, Duration duration, long startTime, int stage) {
        this.uniqueID = UUID;
        this.duration = duration;
        this.startTime = startTime;
        this.stage = stage;
        this.rewards = new ArrayList<>();
        this.goals = new ArrayList<>();
        this.bearer = bearer;
    }

    public static Mission fromSave(TownyMissionsV2 plugin, String bearerString, String UUIDString, @Nullable Long durationTime, long startTime, int stage) {
        Preconditions.checkNotNull(bearerString);
        Preconditions.checkNotNull(UUIDString);

        UUID uuid = null;
        try {
            uuid = UUID.fromString(UUIDString);
        } catch (IllegalArgumentException e) {
            Bukkit.getLogger().severe("Cannot load Mission '"+UUIDString+"' for bearer '"+bearerString+"' as the UUID is not valid!");
            return null;
        }

        UUID bearerUUID = null;
        try {
            bearerUUID = UUID.fromString(bearerString);
        } catch (IllegalArgumentException e) {
            Bukkit.getLogger().severe("Could not load Mission '"+UUIDString+"' as bearer '"+bearerString+"' is not a valid UUID!");
            return null;
        }

        MissionBearer bearer = null;
        try {
            bearer = plugin.getMissionManager().getMissionBearer(bearerUUID);
        } catch (IllegalArgumentException e) {
            return null;
        }

        Duration duration = Mission.INFINITE_DURATION;
        if(durationTime!=null)
            duration = Duration.of(durationTime,ChronoUnit.MILLIS);

        return new Mission(bearer,uuid,duration,startTime,stage);

    }

    private void init() {
        for(MissionGoal missionGoal : goals) {
            missionGoal.setMission(this);
        }
    }

    public void start() {
        getCurrentGoal().start();
    }

    @Override
    public void distributeRewards() {
        for(MissionReward reward : rewards) {
            reward.handReward(bearer);
        }
    }

    @Override
    public List<AbstractReward> getRewards() {
        return new ArrayList<>(rewards);
    }

    public void addRewards(List<MissionReward> rewards) {
        this.rewards.addAll(rewards);
    }

    public List<MissionReward> getMissionRewards() {
        return rewards;
    }

    @Override
    public void complete() {
        displayCompletion();
        distributeRewards();
        addCooldown();
    }

    private void addCooldown() {
        long endTime = getStartTime() + getDuration().toMillis();
        long timeLeft = endTime - System.currentTimeMillis();

        bearer.setCooldown(Duration.of(timeLeft, ChronoUnit.MILLIS));
    }

    private void displayCompletion() {
        bearer.playSound(MissionSound.MISSION_COMPLETION.getSound());

        boolean broadcasted = false;
        if(bearer.shouldBroadcast()) {
            broadcasted = true;
            Bukkit.broadcastMessage(bearer.getCompletionMessage());
        }

        if(!broadcasted) {
            bearer.sendMessage(bearer.getCompletionMessage());
        }
    }

    public String getTitle() {
        return bearer.getName() + " Mission";
    }

    public Duration getDuration() {
        return duration;
    }

    public List<MissionGoal> getGoals() {
        return goals;
    }

    public MissionGoal getCurrentGoal() {
        if(missionOver())
            return null;

        return goals.get(stage);
    }

    public MissionBearer getMissionBearer() {
        return bearer;
    }

    public boolean missionOver() {
        if(missionExpired())
            return true;

        return stage >= this.goals.size();
    }

    public boolean missionExpired() {

        if(duration.equals(INFINITE_DURATION))
            return false;

        long instant = System.currentTimeMillis();
        long endTime = startTime + duration.toMillis();

        return instant >= endTime;
    }

    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = stage;

        if(missionOver())
            complete();
    }

    public boolean infiniteDuration() {
        return this.duration.equals(INFINITE_DURATION);
    }

    public void nextStage() {

        if(missionOver())
            return;

        stage++;

        if(missionOver()) {
            complete();
        } else {
            getCurrentGoal().start();
        }
    }

    public String toString() {
        return "Mission: "+getUniqueID() + System.lineSeparator() +
                "Complete: " + missionOver() + System.lineSeparator() +
                "Expired: " + missionExpired() + System.lineSeparator() +
                "Goals: " + getGoals() + System.lineSeparator() +
                "Stage: " + getStage() + System.lineSeparator() +
                "Current-Goal: " + getCurrentGoal() + System.lineSeparator() +
                "Rewards: " + this.rewards + System.lineSeparator() +
                "Started-At: "+this.startTime + System.lineSeparator() +
                "Duration: "+this.duration.toMillis() + System.lineSeparator();
    }

    public long getStartTime() {
        return startTime;
    }

    public void forceComplete() {
        for(MissionGoal goal : goals) {
            if(!goal.isComplete())
                goal.forceComplete();
        }

        complete();
    }

    //INSERT INTO Mission VALUES('UUID','Mission Bearer','Duration','Start Time','Stage')

    @Override
    public void loadParameters(PreparedStatement statement) throws SQLException {
        statement.setString(1,getUniqueID().toString());
        statement.setString(2,getMissionBearer().getUniqueID().toString());
        if(duration==null || infiniteDuration()) {
            statement.setNull(3, Types.NULL);
        } else {
            statement.setLong(3,duration.toMillis());
        }
        statement.setLong(4,startTime);
        statement.setInt(5,stage);

        statement.addBatch();
    }

    public static class Builder {
        private final List<MissionReward> rewards;
        private final List<MissionGoal> goals;
        private final MissionBearer bearer;
        private final Duration duration;

        public Builder(MissionBearer bearer, Duration duration) {
            this.bearer = bearer;
            this.duration = duration;
            this.rewards = new ArrayList<>();
            this.goals = new ArrayList<>();
        }

        public Builder(MissionBearer bearer) {
            this(bearer,INFINITE_DURATION);
        }

        public Builder addReward(MissionReward goalReward) {
            this.rewards.add(goalReward);
            return this;
        }

        public Builder appendGoal(MissionGoal goal) {
            this.goals.add(goal);
            return this;
        }

        public Builder insertGoal(int stage, MissionGoal goal) {
            if(stage<=0)
                throw new ArrayIndexOutOfBoundsException("Stage must be positive");

            stage = Math.min(this.goals.size()-1,stage);
            return this;
        }

        public Mission build() {
            return new Mission(this);
        }
    }

    public UUID getUniqueID() {
        return uniqueID;
    }
}
