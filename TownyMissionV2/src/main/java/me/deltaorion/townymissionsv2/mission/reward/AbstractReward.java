package me.deltaorion.townymissionsv2.mission.reward;

import com.google.common.base.Preconditions;
import me.deltaorion.townymissionsv2.configuration.ConfigurationException;
import me.deltaorion.townymissionsv2.configuration.Message;
import me.deltaorion.townymissionsv2.mission.reward.type.RewardEnum;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class AbstractReward implements ConfigurationSerializable {

    private final double total;
    private final RewardType rewardType;
    private final UUID uniqueID;

    public AbstractReward(double total, RewardType type) {

        Preconditions.checkNotNull(type);

        this.total = total;
        this.rewardType = type;
        this.uniqueID = UUID.randomUUID();
    }

    public AbstractReward(double total, RewardType type, UUID uuid) {
        Preconditions.checkNotNull(type);
        this.total = total;
        this.rewardType = type;
        this.uniqueID = uuid;
    }

    protected static RewardType deserializeRewardType(ConfigurationSection section) throws ConfigurationException {
        if(!section.contains("type"))
            throw new ConfigurationException(section,"type");

        RewardEnum type = null;
        try {
            type = RewardEnum.valueOf(section.getString("type").toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ConfigurationException("type",section.getString("type"),"Unknown Reward Type");
        }
        return RewardEnum.get(type,section);
    }

    protected void handRewards(UUID uuid) {
        rewardType.handReward(uuid,total);
        displayReward(uuid,this,total);
    }

    protected void handRewardsOnContribution(Map<UUID,Integer> players) {

        final int contributionTotal = players.values()
                .stream()
                .reduce(0, Integer::sum);

        if(contributionTotal==0) {
            handRewardsEqually(new ArrayList<>(players.keySet()));
        }

        players.keySet().forEach(user -> {
            double userContribution = players.get(user);
            double contributionPercent = userContribution/contributionTotal;
            double userReward = contributionPercent * total;
            rewardType.handReward(user,userReward);
            displayReward(user,this,userReward);
        });
    }

    protected void handRewardsEqually(List<UUID> players) {
        for(UUID player : players) {
            double percent = 1.0/players.size();
            double userReward = total * percent;
            rewardType.handReward(player,userReward);
            displayReward(player,this,userReward);
        }
    }

    public String getRewardName() {
        return rewardType.getName();
    }

    public double getTotal() {
        return total;
    }

    protected void displayReward(UUID uuid, AbstractReward reward, double amount) {
        sendMessage(uuid, Message.MISSION_REWARD_RECEIVE.getMessage(String.format("%.2f",amount),reward.getRewardName()));
    }

    protected abstract void sendMessage(UUID uuid, String message);

    public RewardType getRewardType() {
        return rewardType;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String,Object> serialized = new LinkedHashMap<>();
        serialized.put("type", RewardEnum.fromClass(getRewardType()));
        serialized.putAll(getRewardType().serialize());
        serialized.put("total",getTotal());
        return serialized;
    }

    public UUID getUniqueID() {
        return uniqueID;
    }
}
