package me.deltaorion.townymissionsv2.mission.reward;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.Map;
import java.util.UUID;

public interface RewardType extends ConfigurationSerializable, Cloneable {

    public String getName();

    public void handReward(UUID user, double amount);

    public UUID getUniqueID();

    public RewardType copy();
}
