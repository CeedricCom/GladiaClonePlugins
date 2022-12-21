package me.deltaorion.townymissionsv2.mission.reward;

import org.bukkit.Bukkit;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class OfflineReward {

    private final RewardType type;
    private final double total;
    private final UUID player;
    private boolean handed = false;

    public OfflineReward(RewardType type, double total, UUID player) {
        this.type = type;
        this.total = total;
        this.player = player;
    }

    public static OfflineReward fromSave(RewardType type, double amount, String playerID) {
        UUID player = null;
        try {
            player = UUID.fromString(playerID);
        } catch (IllegalArgumentException e) {
            Bukkit.getLogger().severe("Could not read playerID '"+playerID+"' for offline reward as it is not a valid UUID");
            return null;
        }

        return new OfflineReward(type,amount,player);
    }

    public void handReward() {
        if(handed)
            throw new IllegalStateException("Cannot Hand out Offline reward as it has been handed out before!");

        handed = true;
        type.handReward(player,total);
    }

    public UUID getPlayer() {
        return player;
    }

    //INSERT INTO OfflineReward(PlayerID,Amount,RewardType) VALUES('DeltaOrion',500,'aaa');

    public void loadParameters(PreparedStatement statement, UUID rewardType) throws SQLException {
        statement.setString(1,player.toString());
        statement.setDouble(2,total);
        statement.setString(3,rewardType.toString());
        statement.addBatch();
    }

    public RewardType getType() {
        return type;
    }
}
