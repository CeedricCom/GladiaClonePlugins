package me.deltaorion.townymissionsv2.mission.reward.type;

import com.google.common.base.Preconditions;
import com.palmergames.bukkit.towny.TownyEconomyHandler;
import com.palmergames.bukkit.towny.object.EconomyHandler;
import me.deltaorion.townymissionsv2.mission.reward.RewardType;
import org.bukkit.Bukkit;

import java.util.UUID;

public abstract class EconomyReward implements RewardType {

    private final UUID uuid;

    protected EconomyReward() {
        this.uuid = UUID.randomUUID();
    }

    protected EconomyReward(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public void handReward(UUID user, double amount) {
        Preconditions.checkNotNull(user);

        if(!TownyEconomyHandler.isActive())
            Bukkit.getLogger().warning("Cannot give money as there is no relevant economy plugin");

        EconomyHandler handler = getHandler(user);

        if(handler==null)
            return;

        handler.getAccount().deposit(amount,"Mission Reward Payment");
    }

    public abstract EconomyHandler getHandler(UUID user);

    public UUID getUniqueID() {
        return uuid;
    }

}
