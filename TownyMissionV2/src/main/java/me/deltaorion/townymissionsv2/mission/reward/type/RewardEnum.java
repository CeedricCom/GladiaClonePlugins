package me.deltaorion.townymissionsv2.mission.reward.type;

import me.deltaorion.townymissionsv2.configuration.ConfigurationException;
import me.deltaorion.townymissionsv2.mission.reward.RewardType;
import org.bukkit.configuration.ConfigurationSection;

public enum RewardEnum {
    COMMAND,
    EXP,
    GOVERNMENT_BANK,
    ITEM,
    MONEY,
    TEST
    ;

    public static RewardEnum fromClass(RewardType rewardType) {
        if(rewardType instanceof TestReward)
            return RewardEnum.TEST;

        if(rewardType instanceof MoneyReward)
            return RewardEnum.MONEY;

        if(rewardType instanceof ExperienceReward)
            return RewardEnum.EXP;

        if(rewardType instanceof CommandReward)
            return RewardEnum.COMMAND;

        if(rewardType instanceof GovernmentBankReward)
            return RewardEnum.GOVERNMENT_BANK;

        if(rewardType instanceof ItemReward)
            return RewardEnum.ITEM;

        throw new IllegalArgumentException("Unknown Reward Type '"+rewardType+"'");
    }

    public static RewardType get(RewardEnum type, ConfigurationSection section) {
        switch (type) {
            case ITEM:
                return ItemReward.deserialize(section);
            case COMMAND:
                return CommandReward.deserialize(section);
            case EXP:
                return ExperienceReward.deserialize(section);
            case MONEY:
                return MoneyReward.deserialize(section);
            case TEST:
                return TestReward.deserialize(section);
            case GOVERNMENT_BANK:
                return GovernmentBankReward.deserialize(section);
        }
        throw new IllegalStateException("Reward Type '"+type+"' has not been linked to RewardEnum Deserialization");
    }
}
