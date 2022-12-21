package me.deltaorion.townymissionsv2.storage.sql;

import me.deltaorion.townymissionsv2.Definition;
import me.deltaorion.townymissionsv2.TownyMissionsV2;
import me.deltaorion.townymissionsv2.bearer.BearerType;
import me.deltaorion.townymissionsv2.bearer.GovernmentMissionBearer;
import me.deltaorion.townymissionsv2.bearer.MissionBearer;
import me.deltaorion.townymissionsv2.mission.Mission;
import me.deltaorion.townymissionsv2.mission.MissionGoal;
import me.deltaorion.townymissionsv2.mission.chat.ChatGoal;
import me.deltaorion.townymissionsv2.mission.gather.GatherGoal;
import me.deltaorion.townymissionsv2.mission.reward.*;
import me.deltaorion.townymissionsv2.mission.reward.type.*;
import me.deltaorion.townymissionsv2.storage.ConnectionFactory;
import me.deltaorion.townymissionsv2.storage.StorageFactory;
import me.deltaorion.townymissionsv2.storage.StorageImplementation;
import me.deltaorion.townymissionsv2.storage.StorageType;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.*;

public class SqlStorage implements StorageImplementation {

    private final ConnectionFactory connectionFactory;

    private final TownyMissionsV2 plugin;

    public SqlStorage(TownyMissionsV2 plugin, StorageType type) {
        this.connectionFactory = StorageFactory.getConnection(type,plugin);
        this.plugin = plugin;
    }

    @Override
    public void init() {

        connectionFactory.init(plugin);
        applySchemas();
        loadAll();
    }

    private void applySchemas() {
        testApplySchema("sqlite.sql","MissionBearer");
        testApplySchema("chat_goal.sql","ChatGoal");
        testApplySchema("gather_goal.sql","GatherGoal");
        testApplySchema("command_reward.sql","CommandReward");
        testApplySchema("item_reward.sql","ItemReward");
    }

    private void applySchema(String schemaFileName) throws IOException, SQLException {
        List<String> statements;

        try (InputStream is = this.plugin.getResource(schemaFileName)) {
            if (is == null) {
                throw new IOException("Couldn't locate schema file for " + this.connectionFactory.getImplementationName());
            }

            statements = SchemaReader.getStatements(is);
        }


        try (Connection connection = this.connectionFactory.getConnection()) {
            boolean utf8mb4Unsupported = false;

            try (Statement s = connection.createStatement()) {
                for (String query : statements) {
                    s.addBatch(query);
                }

                try {
                    s.executeBatch();
                } catch (BatchUpdateException e) {
                    if (e.getMessage().contains("Unknown character set")) {
                        utf8mb4Unsupported = true;
                    } else {
                        throw e;
                    }
                }
            }

            // try again
            if (utf8mb4Unsupported) {
                try (Statement s = connection.createStatement()) {
                    for (String query : statements) {
                        s.addBatch(query.replace("utf8mb4", "utf8"));
                    }

                    s.executeBatch();
                }
            }
        }
    }

    private final String MISSION_BEARER_INSERT = "REPLACE INTO MissionBearer VALUES(?,?,?,?,?)";

    @Override
    public void saveBearers() {
        long initialTime = System.currentTimeMillis();
        try {
            Connection connection = connectionFactory.getConnection();
            connection.setAutoCommit(false);
            PreparedStatement statement = connection.prepareStatement(MISSION_BEARER_INSERT);
            for(MissionBearer bearer : plugin.getMissionManager().getMissionBearers()) {
                if (bearer instanceof GovernmentMissionBearer) {

                    ((GovernmentMissionBearer) bearer).loadParameters(statement);
                } else {
                    throw new UnsupportedOperationException("No valid way to save mission bearer implementation '" + bearer + "'");
                }
            }

            statement.executeBatch();
            connection.commit();
            statement.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Bukkit.getLogger().info("Total Elapsed Time --- "+(System.currentTimeMillis()-initialTime));
        Bukkit.getLogger().info("Successfully Saved All Mission Bearers");

    }

    private final String MISSION_INSERT = "REPLACE INTO Mission VALUES(?,?,?,?,?)";

    @Override
    public void saveMissions() {

        long initialTime = System.currentTimeMillis();

        try {
            Connection connection = connectionFactory.getConnection();
            connection.setAutoCommit(false);
            PreparedStatement statement = connection.prepareStatement(MISSION_INSERT);

            long loadTime = System.currentTimeMillis();
            for(MissionBearer bearer : plugin.getMissionManager().getMissionBearers()) {
                for(Mission mission : bearer.getMissions()) {
                    mission.loadParameters(statement);
                }
            }
            System.out.println("Load Time: "+(System.currentTimeMillis()-loadTime));

            long batchTime = System.currentTimeMillis();
            statement.executeBatch();
            connection.commit();
            System.out.println("Batch Time: "+(System.currentTimeMillis()-batchTime));
            statement.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Bukkit.getLogger().info("Total Elapsed Time --- "+(System.currentTimeMillis()-initialTime));
        Bukkit.getLogger().info("Saved All Missions");
    }

    private final String GOAL_INSERT_SUP = "REPLACE INTO MissionGoal VALUES(?,?,?,?,?)";
    private final String GATHER_INSERT_SUB = "REPLACE INTO GatherGoal VALUES(?,?,?)";
    private final String CONTRIBUTION_INSERT = "REPLACE INTO Contribution VALUES(?,?,?,?)";
    private final String CHAT_INSERT = "REPLACE INTO ChatGoal VALUES(?,?,?)";

    @Override
    public void saveGoals() {

        long initialTime = System.currentTimeMillis();

        try {
            Connection connection = connectionFactory.getConnection();
            connection.setAutoCommit(false);

            PreparedStatement goalInsert = connection.prepareStatement(GOAL_INSERT_SUP);
            PreparedStatement gatherInsert = connection.prepareStatement(GATHER_INSERT_SUB);
            PreparedStatement contributionInsert = connection.prepareStatement(CONTRIBUTION_INSERT);
            PreparedStatement chatInsert = connection.prepareStatement(CHAT_INSERT);

            for(MissionBearer bearer : plugin.getMissionManager().getMissionBearers()) {
                for(Mission mission : bearer.getMissions()) {
                    for (int stage = 0; stage < mission.getGoals().size(); stage++) {
                        MissionGoal goal = mission.getGoals().get(stage);

                        if (goal instanceof GatherGoal) {
                            GatherGoal g = (GatherGoal) goal;
                            g.loadParameters(goalInsert, gatherInsert, contributionInsert, stage);
                        } else if (goal instanceof ChatGoal) {
                            ChatGoal g = (ChatGoal) goal;
                            g.loadParameters(goalInsert, chatInsert, contributionInsert, stage);
                        } else {
                            throw new UnsupportedOperationException("Cannot save goal '" + goal + "' as its type is not recognised");
                        }
                    }
                }
            }

            goalInsert.executeBatch();
            gatherInsert.executeBatch();
            contributionInsert.executeBatch();
            chatInsert.executeBatch();

            connection.commit();

            goalInsert.close();
            gatherInsert.close();
            contributionInsert.close();
            chatInsert.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Bukkit.getLogger().info("Total Elapsed Time --- "+(System.currentTimeMillis()-initialTime));
        Bukkit.getLogger().info("Saved all goals");
    }

    private final String MISSION_REWARD_INSERT = "REPLACE INTO MissionReward VALUES(?,?,?,?)";

    private final String REWARD_INSERT = "REPLACE INTO RewardType VALUES(?,?)";
    private final String COMMAND_INSERT = "REPLACE INTO CommandReward VALUES(?,?,?)";
    private final String ITEM_INSERT = "REPLACE INTO ItemReward VALUES(?,?)";

    private UUID saveType(RewardType type, PreparedStatement supStatement, PreparedStatement commandStatement, PreparedStatement itemStatement) {
        try {
            UUID supUUID = type.getUniqueID();
            supStatement.setString(1,supUUID.toString());
            supStatement.setString(2, RewardEnum.fromClass(type).toString());
            supStatement.addBatch();

            if(type instanceof CommandReward) {
                CommandReward r = (CommandReward) type;
                r.loadParameters(commandStatement,supUUID);
            } else if(type instanceof ItemReward) {
                ItemReward r = (ItemReward) type;
                r.loadParameters(itemStatement,supUUID);
            }

            return supUUID;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveMissionRewards() {

        long initialTime = System.currentTimeMillis();

        try {
            Connection connection = connectionFactory.getConnection();
            connection.setAutoCommit(false);
            PreparedStatement statement = connection.prepareStatement(MISSION_REWARD_INSERT);
            PreparedStatement supStatement = connection.prepareStatement(REWARD_INSERT);
            PreparedStatement commandStatement = connection.prepareStatement(COMMAND_INSERT);
            PreparedStatement itemStatement = connection.prepareStatement(ITEM_INSERT);

            for(MissionBearer bearer : plugin.getMissionManager().getMissionBearers()) {
                for(Mission mission : bearer.getMissions()) {
                    for (MissionReward reward : mission.getMissionRewards()) {
                        UUID rewardType = saveType(reward.getRewardType(),supStatement,commandStatement,itemStatement);
                        reward.loadParameters(statement, rewardType, mission);
                    }
                }
            }

            supStatement.executeBatch();
            commandStatement.executeBatch();
            itemStatement.executeBatch();
            statement.executeBatch();

            connection.commit();

            statement.close();
            supStatement.close();
            itemStatement.close();
            commandStatement.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Bukkit.getLogger().info("Total Elapsed Time --- "+(System.currentTimeMillis()-initialTime));
        Bukkit.getLogger().info("Saved All Mission Rewards");

    }

    private final String GOAL_REWARD_INSERT = "REPLACE INTO GoalReward VALUES(?,?,?,?,?)";

    @Override
    public void saveGoalRewards() {

        long initialTime = System.currentTimeMillis();

        try {

            Connection connection = connectionFactory.getConnection();
            connection.setAutoCommit(false);

            PreparedStatement statement = connection.prepareStatement(GOAL_REWARD_INSERT);
            PreparedStatement supStatement = connection.prepareStatement(REWARD_INSERT);
            PreparedStatement commandStatement = connection.prepareStatement(COMMAND_INSERT);
            PreparedStatement itemStatement = connection.prepareStatement(ITEM_INSERT);

            for(MissionBearer bearer : plugin.getMissionManager().getMissionBearers()) {
                for (Mission mission : bearer.getMissions()) {
                    for (int stage = 0; stage < mission.getGoals().size(); stage++) {
                        MissionGoal goal = mission.getGoals().get(stage);
                        for (AbstractReward reward : goal.getRewards()) {
                            if (reward instanceof GoalReward) {
                                GoalReward goalReward = (GoalReward) reward;
                                UUID rewardType = saveType(reward.getRewardType(),supStatement,commandStatement,itemStatement);
                                goalReward.loadParameters(statement,stage,goal,rewardType);
                            }
                        }
                    }
                }
            }

            supStatement.executeBatch();
            commandStatement.executeBatch();
            itemStatement.executeBatch();
            statement.executeBatch();

            connection.commit();

            statement.close();
            supStatement.close();
            itemStatement.close();
            commandStatement.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Bukkit.getLogger().info("Total Elapsed Time --- "+(System.currentTimeMillis()-initialTime));
        Bukkit.getLogger().info("Saved All Goal Rewards");

    }

    private final String OFFLINE_REWARD_INSERT = "REPLACE INTO OfflineReward(PlayerID,Amount,RewardType) VALUES(?,?,?)";

    @Override
    public void saveOfflineRewards() {

        long initialTime = System.currentTimeMillis();

        try {
            Connection connection = connectionFactory.getConnection();
            connection.setAutoCommit(false);

            PreparedStatement statement = connection.prepareStatement(OFFLINE_REWARD_INSERT);
            PreparedStatement supStatement = connection.prepareStatement(REWARD_INSERT);
            PreparedStatement commandStatement = connection.prepareStatement(COMMAND_INSERT);
            PreparedStatement itemStatement = connection.prepareStatement(ITEM_INSERT);
            for(OfflineReward reward : OfflineRewardManager.getRewards()) {
                UUID rewardType = saveType(reward.getType(),supStatement,commandStatement,itemStatement);
                reward.loadParameters(statement,rewardType);
            }

            supStatement.executeBatch();
            commandStatement.executeBatch();
            itemStatement.executeBatch();
            statement.executeBatch();

            statement.close();
            supStatement.close();
            itemStatement.close();
            commandStatement.close();

            connection.commit();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Bukkit.getLogger().info("Total Elapsed Time --- "+(System.currentTimeMillis()-initialTime));
        Bukkit.getLogger().info("Saved all offline rewards");
    }


    @Override
    public void saveAll() {

        long initialTime = System.currentTimeMillis();
        dropOfflineRecords();

        saveBearers();
        saveMissions();
        saveGoals();
        saveMissionRewards();
        saveGoalRewards();
        saveOfflineRewards();

        Bukkit.getLogger().info("Total Elapsed Time --- "+(System.currentTimeMillis()-initialTime));
    }

    private final String DELETE_OFFLINE_REWARD = "DELETE FROM OfflineReward";

    private void dropOfflineRecords() {

        Statement statement = null;
        try {
            Connection connection = connectionFactory.getConnection();
            statement = connection.createStatement();
            statement.addBatch(DELETE_OFFLINE_REWARD);

            statement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(statement!=null) {
                try {
                    statement.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
    }

    @Override
    public void loadAll() {

        loadBearers();
        loadMissions();
        loadOfflineRewards();
        dropOfflineRecords();
    }

    private final String BEARER_SELECT = "SELECT * FROM MissionBearer";

    @Override
    public void loadBearers() {

        long initialTime = System.currentTimeMillis();

        try {
            Connection connection = connectionFactory.getConnection();
            PreparedStatement statement = connection.prepareStatement(BEARER_SELECT);
            ResultSet rs = statement.executeQuery();

            while(rs.next()) {

                BearerType type = BearerType.valueOf(rs.getString("BearerType"));

                if(type.equals(BearerType.GOVERNMENT)) {
                    MissionBearer bearer = GovernmentMissionBearer.fromSave(
                            rs.getString("UUID"),
                            rs.getString("PrimaryMission"),
                            rs.getLong("CooldownStart"),
                            rs.getLong("CooldownDuration"));

                    if(bearer!=null)
                        plugin.getMissionManager().loadBearer(bearer);

                } else {
                    throw new UnsupportedOperationException("Could not find saving procedure for '"+rs.getString("BearerType"+"'"));
                }
            }

            rs.close();
            statement.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Bukkit.getLogger().info("Loaded all Bearers, Time Elapsed --- "+(System.currentTimeMillis()-initialTime));
    }

    private final String MISSION_SELECT = "SELECT * FROM Mission";

    //first optimisation, precompile all queries
    //second optimisation create index,

    @Override
    public void loadMissions() {
        long initialTime = System.currentTimeMillis();
        Bukkit.getLogger().info("----------------------");
        Bukkit.getLogger().info("Loading All Missions - This might take a minute!");
        Bukkit.getLogger().info("----------------------");

        try {
            Connection connection = connectionFactory.getConnection();
            Statement missionStatement = connection.createStatement();
            PreparedStatement goalStatement = connection.prepareStatement(GOAL_SELECT);
            PreparedStatement contributionStatement = connection.prepareStatement(CONTRIBUTION_SELECT);
            PreparedStatement goalRewardStatement = connection.prepareStatement(GOAL_REWARD_SELECT);
            PreparedStatement missionRewardStatement = connection.prepareStatement(MISSION_REWARD_SELECT);

            ResultSet missionSet = missionStatement.executeQuery(MISSION_SELECT);
            while(missionSet.next()) {

                Mission mission = Mission.fromSave(plugin,
                        missionSet.getString("MissionBearer"),
                        missionSet.getString("UUID"),
                        missionSet.getObject("Duration") == null ? null : missionSet.getLong("Duration"),
                        missionSet.getLong("StartTime"),
                        missionSet.getInt("Stage")
                );

                if(mission==null)
                    continue;

                List<MissionGoal> goals = loadGoals(mission,connection, goalStatement, contributionStatement, goalRewardStatement);
                if(goals==null)
                    continue;

                mission.getGoals().addAll(goals);

                List<MissionReward> rewards = getRewards(mission,connection, missionRewardStatement);
                if(rewards==null)
                    continue;

                mission.addRewards(rewards);
                mission.getMissionBearer().addMission(mission);

            }

            missionSet.close();
            missionStatement.close();
            goalStatement.close();
            contributionStatement.close();
            goalRewardStatement.close();
            missionRewardStatement.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Bukkit.getLogger().info("Loaded all Missions, Time Elapsed --- "+(System.currentTimeMillis()-initialTime));
    }

    private final String OFFLINE_REWARD_SELECT = "SELECT OfflineReward.RewardID, OfflineReward.PlayerID, OfflineReward.Amount, OfflineReward.RewardType, RewardType.TypeID, RewardType.Type, CommandReward.CommandLine, CommandReward.Name, ItemReward.ItemStack\n" +
            "FROM OfflineReward " +
            "INNER JOIN RewardType " +
            "ON RewardType.TypeID=OfflineReward.RewardType " +
            "LEFT JOIN CommandReward " +
            "ON CommandReward.TypeID = RewardType.TypeID " +
            "LEFT JOIN ItemReward " +
            "ON ItemReward.TypeID = RewardType.TypeID";

    @Override
    public void loadOfflineRewards() {
        try {
            Connection connection = connectionFactory.getConnection();
            PreparedStatement offlineRewardStatement = connection.prepareStatement(OFFLINE_REWARD_SELECT);
            ResultSet rs = offlineRewardStatement.executeQuery();
            while(rs.next()) {
                RewardType type = getType(rs);
                OfflineReward reward = OfflineReward.fromSave(type,
                        rs.getDouble("Amount"),
                        rs.getString("PlayerID"));

                if(reward!=null)
                    OfflineRewardManager.give(reward);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private final String GOAL_SELECT = "SELECT MissionGoal.Mission, MissionGoal.Stage, MissionGoal.GoalAmount, MissionGoal.Definition, MissionGoal.Progress, ChatGoal.Word, GatherGoal.Material " +
            "FROM MissionGoal " +
            "LEFT JOIN ChatGoal " +
            "ON ChatGoal.Mission = MissionGoal.Mission AND ChatGoal.Stage = MissionGoal.Stage " +
            "LEFT JOIN GatherGoal " +
            "ON GatherGoal.Mission = MissionGoal.Mission AND GatherGoal.Stage = MissionGoal.Stage " +
            "WHERE MissionGoal.Mission = ? " +
            "ORDER BY MissionGoal.Stage ASC ";

    private List<MissionGoal> loadGoals(Mission mission, Connection connection, PreparedStatement goalStatement, PreparedStatement contributionStatement,
                                        PreparedStatement goalRewardStatement) throws SQLException {

        goalStatement.setString(1,mission.getUniqueID().toString());
        ResultSet goalSet = goalStatement.executeQuery();

        List<MissionGoal> goals = new ArrayList<>();

        while(goalSet.next()) {
            Definition definition = Definition.valueOf(goalSet.getString("Definition"));
            int stage = goalSet.getInt("Stage");
            Map<UUID,Integer> contributions = getContributions(connection, mission, stage, contributionStatement);

            List<GoalReward> rewards = getRewards(mission,stage,connection,goalRewardStatement);

            if(rewards==null) {
                Bukkit.getLogger().severe("Cannot load Mission '"+mission.getUniqueID() + "' as one of the rewards could not be loaded properly!");
                return null;
            }

            MissionGoal goal = null;
            if (definition.equals(Definition.CHAT)) {
                goal = ChatGoal.fromSave(plugin,
                        definition,
                        goalSet.getInt("Progress"),
                        goalSet.getInt("GoalAmount"),
                        goalSet.getString("Word"),
                        contributions,
                        mission,
                        rewards);

            } else if (definition.equals(Definition.GATHER)) {
                goal = GatherGoal.fromSave(plugin,
                        definition,
                        mission.getMissionBearer(),
                        mission,
                        goalSet.getInt("GoalAmount"),
                        goalSet.getInt("Progress"),
                        contributions,
                        goalSet.getString("Material"),
                        rewards);

            } else {
                throw new IllegalStateException("Could not find saving procedure for '"+definition+"' goal definition");
            }

            if(goal==null) {
                Bukkit.getLogger().severe("Cannot load Mission '" + mission.getUniqueID() + "' as one of its goals could not be loaded properly!");
                return null;
            }

            goals.add(goal);
        }

        goalSet.close();
        goalStatement.clearParameters();


        return goals;
    }

    private final String GOAL_REWARD_SELECT = "SELECT GoalReward.RewardID, GoalReward.Goal, GoalReward.Mission, GoalReward.Amount, GoalReward.RewardType, RewardType.TypeID, RewardType.Type, CommandReward.CommandLine, CommandReward.Name, ItemReward.ItemStack\n" +
            "FROM GoalReward " +
            "INNER JOIN RewardType " +
            "ON RewardType.TypeID=GoalReward.RewardType " +
            "LEFT JOIN CommandReward " +
            "ON CommandReward.TypeID = RewardType.TypeID " +
            "LEFT JOIN ItemReward " +
            "ON ItemReward.TypeID = RewardType.TypeID " +
            "WHERE GoalReward.Mission = ? AND GoalReward.Goal = ?";

    private List<GoalReward> getRewards(Mission mission , int stage, Connection connection, PreparedStatement rewardStatement) throws SQLException {
        rewardStatement.setString(1,mission.getUniqueID().toString());
        rewardStatement.setInt(2,stage);

        List<GoalReward> rewards = new ArrayList<>();

        ResultSet rewardSet = rewardStatement.executeQuery();
        while(rewardSet.next()) {
            RewardType type = getType(rewardSet);
            if(type==null) {
                Bukkit.getLogger().severe("Could not read reward for mission goal '"+mission+"' at stage '"+stage+"'");
                return null;
            }


            GoalReward reward = GoalReward.fromSave(rewardSet.getString("RewardID"),rewardSet.getDouble("Amount"),type);
            if(reward==null) {
                Bukkit.getLogger().severe("Could not read reward for mission goal '"+mission+"' at stage '"+stage+"'");
                return null;
            }
            rewards.add(reward);

        }

        rewardSet.close();
        rewardStatement.clearParameters();

        if(rewards.size()==0) {
            Bukkit.getLogger().severe("Could not find rewards for goal");
        }

        return rewards;
    }

    private final String MISSION_REWARD_SELECT = "SELECT MissionReward.RewardID, MissionReward.Mission, MissionReward.Amount, MissionReward.RewardType, RewardType.TypeID, RewardType.Type, CommandReward.CommandLine, CommandReward.Name, ItemReward.ItemStack " +
            "FROM MissionReward " +
            "INNER JOIN RewardType " +
            "ON RewardType.TypeID=MissionReward.RewardType " +
            "LEFT JOIN CommandReward " +
            "ON CommandReward.TypeID = RewardType.TypeID " +
            "LEFT JOIN ItemReward " +
            "ON ItemReward.TypeID = RewardType.TypeID " +
            "WHERE MissionReward.Mission = ?";

    public List<MissionReward> getRewards(Mission mission, Connection connection, PreparedStatement rewardStatement) throws SQLException {
        rewardStatement.setString(1,mission.getUniqueID().toString());

        List<MissionReward> rewards = new ArrayList<>();

        ResultSet rewardSet = rewardStatement.executeQuery();
        while(rewardSet.next()) {
            RewardType type = getType(rewardSet);
            if(type==null) {
                Bukkit.getLogger().severe("Could not read mission reward for mission '"+mission+"'");
                return null;
            }

            MissionReward missionReward = MissionReward.fromSave(rewardSet.getDouble("Amount"),type,rewardSet.getString("RewardID"));
            if(missionReward==null) {
                Bukkit.getLogger().severe("Could not read reward for mission '"+mission.getUniqueID()+"'");
                return null;
            }

            rewards.add(missionReward);
        }

        rewardSet.close();
        rewardStatement.clearParameters();

        return rewards;
    }

    private RewardType getType(ResultSet rewardSet) throws SQLException {

        RewardEnum rewardType = null;
        UUID rewardUUID = null;

        String rewardTypeString = rewardSet.getString("Type");
        String rewardUUIDString = rewardSet.getString("TypeID");

        try {
            rewardType = RewardEnum.valueOf(rewardTypeString);
        } catch (IllegalArgumentException e) {
            Bukkit.getLogger().severe("Could not read reward type '"+rewardTypeString+"'");
            return null;
        }

        try {
            rewardUUID = UUID.fromString(rewardUUIDString);
        } catch (IllegalArgumentException e) {
            Bukkit.getLogger().severe("Could not read reward UUID as it is invalid - '"+rewardUUIDString+"'");
            return null;
        }

        switch (rewardType) {

            case GOVERNMENT_BANK:
                return GovernmentBankReward.fromSave(rewardUUID);
            case TEST:
                return TestReward.fromSave(rewardUUID);
            case MONEY:
                return MoneyReward.fromSave(rewardUUID);
            case EXP:
                return ExperienceReward.fromSave(rewardUUID);
            case COMMAND:
                return CommandReward.fromSave(rewardUUID,
                        rewardSet.getString("CommandLine"),
                        rewardSet.getString("Name"));
            case ITEM:
                return ItemReward.fromSave(rewardUUID,rewardSet.getString("ItemStack"));
            default:
                Bukkit.getLogger().severe("No loading information found for '"+rewardType.toString()+"'");
                return null;

        }
    }

    private final String CONTRIBUTION_SELECT =
            "SELECT * " +
            "FROM Contribution " +
            "WHERE Contribution.Mission = ? AND Contribution.Goal = ? ";

    private Map<UUID, Integer> getContributions(Connection connection, Mission mission, int stage, PreparedStatement statement) throws SQLException {
        Map<UUID,Integer> contributions = new HashMap<>();
        statement.setString(1,mission.getUniqueID().toString());
        statement.setInt(2,stage);
        ResultSet rs = statement.executeQuery();

        while(rs.next()) {
           contributions.put(UUID.fromString(rs.getString("UUID")),rs.getInt("Amount"));
        }

        rs.close();
        statement.clearParameters();

        return contributions;
    }


    @Override
    public boolean tableExists(String table) throws SQLException {
        Connection connection = connectionFactory.getConnection();

        try (ResultSet rs = connection.getMetaData().getTables(connection.getCatalog(), null, "%", null)) {
            while (rs.next()) {
                if (rs.getString(3).equalsIgnoreCase(table)) {
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public void testApplySchema(String location, String table) {
        try {
            if (!tableExists(table)) {
                String implementation = connectionFactory.getImplementationName().toLowerCase(Locale.ROOT);
                applySchema("storage" + File.separator + implementation + File.separator + location);
            }
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void shutdown() {

        saveAll();

        try {
            connectionFactory.shutdown();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
