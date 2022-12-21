package me.deltaorion.townymissionsv2.mission.goal;

import com.google.common.base.Preconditions;
import me.deltaorion.townymissionsv2.bearer.MissionBearer;
import me.deltaorion.townymissionsv2.configuration.Message;
import me.deltaorion.townymissionsv2.display.MinecraftSound;
import me.deltaorion.townymissionsv2.display.MissionSound;
import me.deltaorion.townymissionsv2.mission.Mission;
import me.deltaorion.townymissionsv2.mission.definition.GoalDefinition;
import me.deltaorion.townymissionsv2.mission.reward.AbstractReward;
import me.deltaorion.townymissionsv2.mission.reward.GoalReward;
import me.deltaorion.townymissionsv2.util.RandomHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public abstract class CollectiveGoal extends CompletableGoal {

    private final Map<UUID,Integer> contributions;
    private final List<GoalReward> rewards;
    private final RandomHelper random = new RandomHelper();

    public CollectiveGoal(int goal, MissionBearer master, GoalDefinition definition, List<GoalReward> rewards) {
        super(goal,master,definition);

        Preconditions.checkNotNull(rewards);

        this.contributions = new HashMap<>();
        this.rewards = rewards;
    }



    public CollectiveGoal(int goal, MissionBearer master, GoalDefinition definition) {
        this(goal,master, definition, new ArrayList<>());
    }

    protected CollectiveGoal(MissionBearer master, Mission mission, GoalDefinition definition, int progress, int amount,Map<UUID,Integer> contributions, List<GoalReward> rewards) {
        super(master,mission,definition,amount,progress);
        this.rewards = new ArrayList<>(rewards);
        this.contributions = contributions;

    }

    @Override
    public void start() {
        getMaster().sendMessage(Message.MISSION_GOAL_GIVE.getMessage(ChatColor.stripColor(getDisplayText())));
    }

    @Override
    public void forceComplete() {
        setProgress(getGoal());
        complete();
    }

    @Override
    public int contribute(UUID user, int quantity) {

        if(quantity==0)
            return 0;

        if(isComplete())
            return quantity;

        Preconditions.checkNotNull(user);

        final int diff = getGoal() - getProgress();
        if(diff==0)
            return quantity;

        int leftOver = 0;

        if(quantity>diff) {
            leftOver = quantity - diff;
            quantity = diff;
        }

        setProgress(getProgress() + quantity);

        recordContribution(user,quantity);
        displayContribution(user);


        if(isComplete()) {
            complete();
        }

        return leftOver;
    }

    private void displayContribution(UUID user) {
        Player player = Bukkit.getPlayer(user);
        if(player==null)
            return;

        getRandomContributionSound().playSound(player);
    }

    private MinecraftSound getRandomContributionSound() {
        int choice = random.randomInt(1,3);
        if(choice==1) {
            return MissionSound.MISSION_AUTO_CONTRIBUTE_1.getSound();
        } else if(choice==2) {
            return MissionSound.MISSION_AUTO_CONTRIBUTE_2.getSound();
        } else if(choice==3) {
            return MissionSound.MISSION_AUTO_CONTRIBUTE_3.getSound();
        }

        return MissionSound.MISSION_AUTO_CONTRIBUTE_1.getSound();
    }

    private void recordContribution(UUID user, int quantity) {

        Preconditions.checkNotNull(user);

        if(!this.contributions.containsKey(user)) {
            this.contributions.put(user, quantity);
        } else {
            int retrieved = this.contributions.get(user);
            retrieved = retrieved + quantity;
            this.contributions.put(user,retrieved);
        }
    }

    public Map<UUID, Integer> getContributions() {
        return contributions;
    }

    protected List<GoalReward> getRewardList() {
        return rewards;
    }

    @Override
    public List<AbstractReward> getRewards() {
        return new ArrayList<>(getRewardList());
    }

    @Override
    public void distributeRewards() {
        for(GoalReward goalReward : rewards) {
            goalReward.handRewardsOnContribution(contributions);
        }
    }

    //INSERT INTO Contribution VALUES(Player ID,stage,mission,contribution);

    public void saveContributions(PreparedStatement statement, int stage) throws SQLException {
        for(Map.Entry<UUID,Integer> contribution : contributions.entrySet()) {
            statement.setString(1,contribution.getKey().toString());
            statement.setInt(2,stage);
            statement.setString(3,getMission().getUniqueID().toString());
            statement.setInt(4,contribution.getValue());
            statement.addBatch();
        }
    }




}
