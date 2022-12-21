package me.deltaorion.townymissionsv2.mission.reward.type;

import me.deltaorion.townymissionsv2.configuration.Message;
import me.deltaorion.townymissionsv2.mission.reward.RewardType;
import me.deltaorion.townymissionsv2.util.ExperienceUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class ExperienceReward extends OfflinedReward {

    @Override
    public String getName() {
        return Message.REWARD_EXP_NAME.getMessage();
    }

    @Override
    public RewardType copy() {
        return new ExperienceReward();
    }

    private ExperienceReward(UUID uuid) {
        super(uuid);
    }

    public ExperienceReward() {
        super();
    }

    public static ExperienceReward fromSave(UUID uuid) {
        return new ExperienceReward(uuid);
    }

    @Override
    protected void setOnlineReward(UUID user, double amount) {
        Player player = Bukkit.getPlayer(user);
        final int total = (int) (ExperienceUtil.getTotalExperience(player) + amount);
        ExperienceUtil.setTotalExperience(player, total);
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return new LinkedHashMap<>();
    }

    public static ExperienceReward deserialize(ConfigurationSection section) {
        return new ExperienceReward();
    }
}
