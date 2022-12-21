package me.deltaorion.townymissionsv2.mission.reward.type;

import com.google.common.base.Preconditions;
import me.deltaorion.townymissionsv2.mission.reward.OfflineReward;
import me.deltaorion.townymissionsv2.mission.reward.OfflineRewardManager;
import me.deltaorion.townymissionsv2.mission.reward.RewardType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public abstract class OfflinedReward implements RewardType {

    private final UUID uuid;

    protected OfflinedReward() {
        this.uuid = UUID.randomUUID();
    }

    protected OfflinedReward(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public void handReward(UUID user, double amount) {

        Preconditions.checkNotNull(user);

        if(amount < 0)
            throw new IllegalArgumentException("Amount must be positive!");

        Player player = Bukkit.getPlayer(user);

        if(player==null) {
            setOfflineReward(user,amount);
        } else {
            if(player.isOnline()) {
                setOnlineReward(user,amount);
            } else {
                setOfflineReward(user,amount);
            }
        }
    }

    protected abstract void setOnlineReward(UUID user, double amount);

    protected void setOfflineReward(UUID user, double amount) {
        OfflineReward reward = new OfflineReward(this,amount,user);
        OfflineRewardManager.give(reward);
    }

    @Override
    public UUID getUniqueID() {
        return uuid;
    }
}
