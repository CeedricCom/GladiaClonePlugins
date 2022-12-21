package me.deltaorion.townymissionsv2.mission.reward;

import com.palmergames.bukkit.towny.object.Government;
import com.palmergames.bukkit.towny.object.Resident;
import me.deltaorion.townymissionsv2.bearer.MissionBearer;
import me.deltaorion.townymissionsv2.configuration.ConfigurationException;
import me.deltaorion.townymissionsv2.mission.Mission;
import me.deltaorion.townymissionsv2.util.TownyUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class MissionReward extends AbstractReward {

    public MissionReward(double total, RewardType type) {
        super(total, type);
    }

    public MissionReward(double total, RewardType type, UUID uuid) {
        super(total,type,uuid);
    }

    public static MissionReward fromSave(double total, RewardType type, String uuidString) {

        UUID uuid = null;
        try {
            uuid = UUID.fromString(uuidString);
        } catch (IllegalArgumentException e) {
            Bukkit.getLogger().severe("Cannot load GoalReward '"+uuidString+"' as it is not a valid UUID!");
            return null;
        }
        return new MissionReward(total,type,uuid);
    }

    public void handReward(MissionBearer bearer) {
        super.handRewards(bearer.getUniqueID());
    }

    @Override
    public void sendMessage(UUID uuid, String message) {
        Government government = TownyUtil.getGovernment(uuid);
        for(Resident resident : government.getResidents()) {
            if(resident.isOnline()) {
                resident.getPlayer().sendMessage(message);
            }
        }
    }

    public static MissionReward deserialize(ConfigurationSection section) {
        RewardType type = AbstractReward.deserializeRewardType(section);
        if(!section.contains("total"))
            throw new ConfigurationException(section,"total");

        double total = section.getDouble("total");

        return new MissionReward(total,type);
    }

    public void loadParameters(PreparedStatement statement, UUID rewardType, Mission mission) throws SQLException {
        statement.setString(1,getUniqueID().toString());
        statement.setString(2,mission.getUniqueID().toString());
        statement.setDouble(3,getTotal());
        statement.setString(4,rewardType.toString());
        statement.addBatch();
    }

    @Override
    public MissionReward clone() {
        return new MissionReward(getTotal(),getRewardType().copy());
    }


}
