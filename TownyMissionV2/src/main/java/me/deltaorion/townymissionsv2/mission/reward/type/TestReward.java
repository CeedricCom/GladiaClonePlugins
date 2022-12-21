package me.deltaorion.townymissionsv2.mission.reward.type;

import me.deltaorion.townymissionsv2.mission.reward.RewardType;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class TestReward implements RewardType {

    private final UUID uniqueID;

    public TestReward() {
        this.uniqueID = UUID.randomUUID();
    }

    private TestReward(UUID uuid) {
        this.uniqueID = uuid;
    }

    public static TestReward fromSave(UUID uuid) {
        return new TestReward(uuid);
    }

    @Override
    public String getName() {
        return "Test";
    }

    @Override
    public void handReward(UUID user, double amount) {
        System.out.println("Handed "+amount +" to "+user);
    }

    @Override
    public UUID getUniqueID() {
        return uniqueID;
    }

    @Override
    public RewardType copy() {
        return new TestReward();
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return new LinkedHashMap<>();
    }

    public static TestReward deserialize(ConfigurationSection section) {
        return new TestReward();
    }
}
