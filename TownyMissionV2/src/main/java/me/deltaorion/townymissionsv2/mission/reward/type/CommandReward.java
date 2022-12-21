package me.deltaorion.townymissionsv2.mission.reward.type;

import me.deltaorion.townymissionsv2.configuration.ConfigurationException;
import me.deltaorion.townymissionsv2.configuration.MessageManager;
import me.deltaorion.townymissionsv2.mission.reward.RewardType;
import me.deltaorion.townymissionsv2.storage.Saveable;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

public class CommandReward implements RewardType {

    private final String commandLine;
    private final String name;
    private final UUID uuid;

    private final static String AMOUNT_ARG = "%amount%";
    private final static Pattern REGEX = Pattern.compile(AMOUNT_ARG);

    public CommandReward(String commandLine, String name) {
        this.commandLine = commandLine;
        this.name = name;
        uuid = UUID.randomUUID();
    }

    private CommandReward(String commandLine, String name, UUID uniqueID) {
        this.uuid = uniqueID;
        this.name = name;
        this.commandLine = commandLine;
    }

    public static CommandReward fromSave(UUID uuid, String commandLine, String name) {
        return new CommandReward(commandLine,name,uuid);
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public void handReward(UUID user, double amount) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(user);
        if(player.getName()==null) {
            Bukkit.getLogger().info("Cannot give reward to user '"+user+"'");
            return;
        }
        String formatted = MessageManager.substitutePlaceHolders(commandLine,player.getName());
        formatted = replaceAmount(formatted, (int) amount);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),formatted);
    }

    @Override
    public UUID getUniqueID() {
        return uuid;
    }

    @Override
    public RewardType copy() {
        return new CommandReward(commandLine,name);
    }

    private String replaceAmount(String formatted, int amount) {
        return REGEX.matcher(formatted).replaceAll(String.valueOf(amount));
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        LinkedHashMap<String,Object> map = new LinkedHashMap<>();
        map.put("command",commandLine);
        map.put("name",name);
        return map;
    }

    public static CommandReward deserialize(ConfigurationSection section) {
        if(!section.contains("command"))
            throw new ConfigurationException(section,"command");

        String commandLine = section.getString("command");

        if(!section.contains("name"))
            throw new ConfigurationException(section,"name");

        String name = section.getString("name");
        return new CommandReward(commandLine,name);
    }

    //INSERT INTO CommandReward VALUES(0,'Give DeltaOrion stone','Stone')

    public void loadParameters(PreparedStatement statement, UUID sup) throws SQLException {
        statement.setString(1,sup.toString());
        statement.setString(2,commandLine);
        statement.setString(3,name);
        statement.addBatch();
    }
}
