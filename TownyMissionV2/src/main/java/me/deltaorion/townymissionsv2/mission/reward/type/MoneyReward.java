package me.deltaorion.townymissionsv2.mission.reward.type;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.EconomyHandler;
import com.palmergames.bukkit.towny.object.Resident;
import me.deltaorion.townymissionsv2.configuration.Message;
import me.deltaorion.townymissionsv2.mission.reward.RewardType;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class MoneyReward extends EconomyReward {

    private final UUID uuid = UUID.randomUUID();

    public MoneyReward() {
        super();
    }

    private MoneyReward(UUID uuid) {
        super(uuid);
    }

    @Override
    public String getName() {
        return Message.REWARD_MONEY_NAME.getMessage();
    }

    @Override
    public UUID getUniqueID() {
        return uuid;
    }

    @Override
    public RewardType copy() {
        return new MoneyReward();
    }

    @Override
    public EconomyHandler getHandler(UUID user) {
        Resident resident = TownyAPI.getInstance().getResident(user);
        if(resident==null) {
            Bukkit.getLogger().info("Cannot hand money to resident '" + user + "'");
        }

        return resident;
    }

    public static MoneyReward fromSave(UUID uuid) {
        return new MoneyReward(uuid);
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return new LinkedHashMap<>();
    }

    public static MoneyReward deserialize(ConfigurationSection section) {
        return new MoneyReward();
    }
}
