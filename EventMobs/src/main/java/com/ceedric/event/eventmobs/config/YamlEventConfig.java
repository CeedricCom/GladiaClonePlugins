package com.ceedric.event.eventmobs.config;

import com.ceedric.event.eventmobs.BukkitUtil;
import com.ceedric.event.eventmobs.EventsPlugin;
import com.ceedric.event.eventmobs.model.boss.BossEvent;
import com.ceedric.event.eventmobs.model.Event;
import com.ceedric.event.eventmobs.model.EventType;
import com.ceedric.event.eventmobs.model.boss.BossSideEnum;
import com.ceedric.event.eventmobs.model.boss.BossStart;
import com.ceedric.event.eventmobs.model.koth.KothEvent;
import com.ceedric.event.eventmobs.model.reward.*;
import me.deltaorion.common.util.DurationParser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;
import java.util.*;

public class YamlEventConfig implements EventConfig {

    private final EventsPlugin plugin;
    private final List<Event> events;

    public YamlEventConfig(EventsPlugin plugin) {
        this.plugin = plugin;
        this.events = new ArrayList<>();

        reload();
    }

    private void loadConfig() {
        events.clear();
        FileConfiguration configuration = plugin.getConfig();
        events.addAll(loadEvents(configuration.getConfigurationSection("events")));
    }

    private Collection<Event> loadEvents(ConfigurationSection bossesSection) {
        List<Event> events = new ArrayList<>();
        for(String key : bossesSection.getKeys(false)) {
            ConfigurationSection eventSection = bossesSection.getConfigurationSection(key);
            EventType type = EventType.valueOf(eventSection.getString("type").toUpperCase(Locale.ROOT));
            if (type.equals(EventType.BOSS)) {
                Event event = loadBossEvent(key, eventSection);
                events.add(event);
            } else if(type.equals(EventType.KOTH)) {
                Event event = loadKothEvent(key,eventSection);
                events.add(event);
            }
        }

        return events;
    }

    private Event loadKothEvent(String name, ConfigurationSection kothSection) {
        String worldName = kothSection.getString("world");
        World world = BukkitUtil.getWorld(worldName);
        if(world==null) {
            Bukkit.getLogger().severe("Could not load world "+worldName);
            return null;
        }
        KothEvent event = new KothEvent(name,world);
        loadDefaults(event,kothSection);

        int maxPlayers = kothSection.getInt("max-players");
        Duration respawnTime = DurationParser.parseDuration(kothSection.getString("respawn-time"));
        List<String> startCommands = kothSection.getStringList("start-commands");

        for(String startCommand: startCommands) {
            event.addStartCommand(startCommand);
        }

        event.setPlayerCap(maxPlayers);
        event.setRespawnCooldown(respawnTime);

        return event;
    }

    private void loadDefaults(Event event, ConfigurationSection eventSection) {
        for(TopNReward reward : loadNRewards(eventSection.getConfigurationSection("topNRewards"))) {
            event.addReward(reward);
        }

        String commandName = eventSection.getString("command-name");
        String displayName = ChatColor.translateAlternateColorCodes('&',eventSection.getString("display-name"));
        Location spawnLocation = loadLocation(event.getWorld(),eventSection.getConfigurationSection("spawn-location"));
        boolean enabled = eventSection.getBoolean("enabled");

        event.setSpawnLocation(spawnLocation);
        event.setCommandName(commandName);
        event.setDisplayName(displayName);
        event.setEnabled(enabled);
    }

    private Event loadBossEvent(String name, ConfigurationSection bossSection) {
        String worldName = bossSection.getString("world");
        World world = BukkitUtil.getWorld(worldName);
        if(world==null) {
            Bukkit.getLogger().severe("Could not load world "+worldName);
            return null;
        }
        BossEvent event = new BossEvent(name,world);
        loadDefaults(event,bossSection);

        ConfigurationSection startSection = bossSection.getConfigurationSection("bosses");
        for(String key : startSection.getKeys(false)) {
            BossStart start = loadBossStart(world,startSection.getConfigurationSection(key));
            event.addBossStart(start);
        }

        for(Map.Entry<BossSideEnum,String> bossSideName : loadNames(bossSection.getConfigurationSection("names")).entrySet()) {
            event.addName(bossSideName.getKey(),bossSideName.getValue());
        }

        return event;
    }

    private BossStart loadBossStart(World world, ConfigurationSection bossSection) {
        String name = bossSection.getString("name");
        Location spawnLocation = loadLocation(world, bossSection.getConfigurationSection("spawn-location"));
        return new BossStart(name,spawnLocation);
    }

    private List<TopNReward> loadNRewards(ConfigurationSection topNRewardsSection) {
        List<TopNReward> rewards = new ArrayList<>();
        for(String key : topNRewardsSection.getKeys(false)) {
            ConfigurationSection topNSection = topNRewardsSection.getConfigurationSection(key);
            int n = topNSection.getInt("n");
            TopNReward reward = new TopNReward(n);
            for(Reward r : loadRewards(topNSection.getConfigurationSection("rewards"))) {
                reward.addReward(r);
            }
            rewards.add(reward);
        }
        return rewards;
    }

    private List<Reward> loadRewards(ConfigurationSection rewardSect) {
        List<Reward> rewards = new ArrayList<>();
        for(String key : rewardSect.getKeys(false)) {
            ConfigurationSection reward = rewardSect.getConfigurationSection(key);
            RewardType type = RewardType.valueOf(reward.getString("type").toUpperCase(Locale.ROOT));
            switch (type) {
                case ITEM -> rewards.add(loadItem(reward));
                case COMMAND -> rewards.add(loadCommand(reward));
                case SKULL -> rewards.add(loadSkull(reward));
                case CUSTOM_ITEM -> rewards.add(loadCustom(reward));
            }
        }

        return rewards;
    }

    private CommandReward loadCommand(ConfigurationSection reward) {
        String command = reward.getString("command");
        String name = reward.getString("name");
        return new CommandReward(command, name);
    }

    private ItemReward loadItem(ConfigurationSection reward) {
        ItemStack itemStack = reward.getItemStack("itemstack");
        int amount = reward.getInt("amount");
        String name = reward.getString("name");
        if(name==null)
            name = "";

        return new ItemReward(ChatColor.translateAlternateColorCodes('&',name), itemStack,amount);
    }

    public SkullReward loadSkull(ConfigurationSection reward) {
        ItemStack itemStack = reward.getItemStack("itemstack");
        int amount = reward.getInt("amount");
        String name = reward.getString("name");
        if(name==null)
            name = "";

        String skullTexture = reward.getString("texture");
        return new SkullReward(ChatColor.translateAlternateColorCodes('&',name), itemStack,amount,skullTexture);
    }

    public CustomItemReward loadCustom(ConfigurationSection reward) {
        ItemStack itemStack = reward.getItemStack("itemstack");
        int amount = reward.getInt("amount");
        String name = reward.getString("name");
        if(name==null)
            name = "";

        String customItemName = reward.getString("custom-item");
        return new CustomItemReward(ChatColor.translateAlternateColorCodes('&',name), itemStack,amount,customItemName,plugin.getCustomItemManager());
    }



    private Map<BossSideEnum, String> loadNames(ConfigurationSection nameSection) {
        Map<BossSideEnum,String> names = new HashMap<>();
        for(String key : nameSection.getKeys(false)) {
            BossSideEnum side = BossSideEnum.valueOf(key.toUpperCase());
            names.put(side,nameSection.getString(key));
        }
        return names;
    }

    private Location loadLocation(World world ,ConfigurationSection location) {
        double x = location.getDouble("x");
        double y = location.getDouble("y");
        double z = location.getDouble("z");
        double yaw = location.getDouble("yaw");
        double pitch = location.getDouble("pitch");

        return new Location(world,x,y,z, (float) yaw, (float) pitch);
    }

    @Override
    public List<Event> getEvents() {
        return events;
    }

    @Override
    public void reload() {
        plugin.reloadConfig();
        loadConfig();
    }
}
