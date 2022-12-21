package me.deltaorion.townymissionsv2.mission.reward;

import me.deltaorion.townymissionsv2.configuration.ConfigurationException;
import me.deltaorion.townymissionsv2.mission.MissionGoal;
import me.deltaorion.townymissionsv2.mission.reward.type.RewardEnum;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GoalReward extends AbstractReward {

    public GoalReward(double total, RewardType type) {
        super(total,type);
    }

    private GoalReward(double total, RewardType type, UUID uuid) {
        super(total,type,uuid);
    }

    public static GoalReward fromSave(String uuidString, double amount, RewardType type) {
        UUID uuid = null;
        try {
            uuid = UUID.fromString(uuidString);
        } catch (IllegalArgumentException e) {
            Bukkit.getLogger().severe("Cannot load GoalReward '"+uuidString+"' as it is not a valid UUID!");
            return null;
        }

        return new GoalReward(amount,type,uuid);
    }

    public void handRewardsOnContribution(Map<UUID,Integer> players) {
        super.handRewardsOnContribution(players);
    }

    public void handRewardsEqually(List<UUID> players) {
        super.handRewardsEqually(players);
    }

    @Override
    protected void sendMessage(UUID uuid, String message) {
        Player player = Bukkit.getPlayer(uuid);
        if(player!=null) {
            if(player.isOnline()) {
                player.sendMessage(message);
            }
        }
    }

    public static GoalReward deserialize(ConfigurationSection section) {
        RewardType type = AbstractReward.deserializeRewardType(section);
        if(!section.contains("total"))
            throw new ConfigurationException(section,"total");

        double total = section.getDouble("total");

        return new GoalReward(total,type);
    }

    //INSERT INTO GoalReward(RewardID,Goal,Mission,RewardType,Amount) VALUES(0,0,'Gamer','aaa',50)

    public void loadParameters(PreparedStatement statement, int stage, MissionGoal goal, UUID rewardType) throws SQLException {
        statement.setString(1,getUniqueID().toString());
        statement.setInt(2,stage);
        statement.setString(3,goal.getMission().getUniqueID().toString());
        statement.setString(4,rewardType.toString());
        statement.setDouble(5,getTotal());
        statement.addBatch();
    }

    public String toString() {
        return "Reward: "+RewardEnum.fromClass(getRewardType()) + " Total: "+getTotal();
    }

    @Override
    public GoalReward clone() {
        return new GoalReward(this.getTotal(),this.getRewardType().copy());
    }
}
