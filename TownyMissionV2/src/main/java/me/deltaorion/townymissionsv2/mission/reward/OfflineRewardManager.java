package me.deltaorion.townymissionsv2.mission.reward;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class OfflineRewardManager {
    private final static HashMap<UUID, List<OfflineReward>> rewards;

    static {
        rewards = new HashMap<>();
    }

    public static void give(OfflineReward reward) {

        Preconditions.checkNotNull(reward);

        List<OfflineReward> rewardsList;
        if(rewards.containsKey(reward.getPlayer())) {
            rewardsList = rewards.get(reward.getPlayer());
        } else {
            rewardsList = new ArrayList<>();
            rewards.put(reward.getPlayer(),rewardsList);
        }

        rewardsList.add(reward);
    }

    @Nullable
    public static List<OfflineReward> getAndRemove(UUID player) {
        Preconditions.checkNotNull(player);

        List<OfflineReward> rewardsList = rewards.get(player);
        if(rewardsList==null) {
            return null;
        } else {
            rewards.remove(player);
            return rewardsList;
        }
    }

    @Nullable
    public static List<OfflineReward> get(UUID player) {
        Preconditions.checkNotNull(player);

        return rewards.get(player);
    }

    public static Collection<OfflineReward> getRewards() {
        List<OfflineReward> r = new ArrayList<>();
        for(List<OfflineReward> rewardList : rewards.values()) {
            r.addAll(rewardList);
        }

        return r;
    }
}
