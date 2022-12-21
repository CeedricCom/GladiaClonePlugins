package me.deltaorion.townymissionsv2.storage;

import me.deltaorion.townymissionsv2.TownyMissionsV2;
import me.deltaorion.townymissionsv2.bearer.MissionBearer;
import me.deltaorion.townymissionsv2.mission.Mission;
import me.deltaorion.townymissionsv2.mission.MissionGoal;
import me.deltaorion.townymissionsv2.mission.reward.GoalReward;
import me.deltaorion.townymissionsv2.mission.reward.MissionReward;
import me.deltaorion.townymissionsv2.mission.reward.OfflineReward;
import me.deltaorion.townymissionsv2.mission.reward.OfflineRewardManager;

import java.sql.SQLException;

public interface StorageImplementation {

    public void init();

    public void saveBearers();

    public void saveMissions();

    public void saveGoals();

    public void saveMissionRewards();

    public void saveGoalRewards();

    public void saveOfflineRewards();

    public void saveAll();

    public void loadAll();

    public void loadBearers();

    public void loadMissions();

    public void loadOfflineRewards();

    public boolean tableExists(String table) throws SQLException;

    public void testApplySchema(String location, String table);

    void shutdown();
}
