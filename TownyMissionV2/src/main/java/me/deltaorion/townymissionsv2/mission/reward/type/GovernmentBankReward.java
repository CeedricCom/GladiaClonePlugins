package me.deltaorion.townymissionsv2.mission.reward.type;

import com.palmergames.bukkit.towny.object.EconomyHandler;
import me.deltaorion.townymissionsv2.configuration.Message;
import me.deltaorion.townymissionsv2.mission.reward.RewardType;
import me.deltaorion.townymissionsv2.util.TownyUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class GovernmentBankReward extends EconomyReward {

    public GovernmentBankReward() {
        super();
    }

    private GovernmentBankReward(UUID uuid) {
        super(uuid);
    }

    public static RewardType fromSave(UUID rewardUUID) {
        return new GovernmentBankReward(rewardUUID);
    }

    @Override
    public String getName() {
        return Message.REWARD_GOV_BANK_NAME.getMessage();
    }

    @Override
    public RewardType copy() {
        return new GovernmentBankReward();
    }

    @Override
    public EconomyHandler getHandler(UUID user) {
        return TownyUtil.getGovernment(user);
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return new LinkedHashMap<>();
    }

    public static GovernmentBankReward deserialize(ConfigurationSection section) {
        return new GovernmentBankReward();
    }
}
