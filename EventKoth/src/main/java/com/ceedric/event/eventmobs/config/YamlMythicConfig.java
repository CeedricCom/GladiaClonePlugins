package com.ceedric.event.eventmobs.config;

import com.ceedric.event.eventmobs.EventMobs;
import com.ceedric.event.eventmobs.model.MythicBoss;
import com.ceedric.event.eventmobs.model.participant.BossSide;
import com.ceedric.event.eventmobs.model.reward.*;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class YamlMythicConfig implements MythicConfig {

    private final EventMobs plugin;
    private final List<MythicBoss> bosses;
    private Location spawnLocation;
    private MythicMob boss;
    private World world;
    private final Map<BossSide,String> names;

    public YamlMythicConfig(EventMobs plugin) {
        this.plugin = plugin;
        this.bosses = new ArrayList<>();
        this.names = new HashMap<>();

        reload();
    }

    private void loadConfig() {
        bosses.clear();
        names.clear();

        FileConfiguration configuration = plugin.getConfig();
        world = Bukkit.getWorld(configuration.getString("world"));
        boss = MythicBukkit.inst().getMobManager().getMythicMob(configuration.getString("boss")).orElse(null);
        spawnLocation = loadLocation(world,configuration.getConfigurationSection("spawn-location"));
        names.putAll(loadNames(configuration.getConfigurationSection("names")));
        bosses.addAll(loadBosses(configuration.getConfigurationSection("bosses")));
    }

    private Collection<MythicBoss> loadBosses(ConfigurationSection bossesSection) {
        List<MythicBoss> bosses = new ArrayList<>();
        for(String key : bossesSection.getKeys(false)) {
            ConfigurationSection bossSection = bossesSection.getConfigurationSection(key);
            String name = bossSection.getString("name");
            MythicBoss boss = new MythicBoss(name);
            for(TopNReward reward : loadNRewards(bossSection.getConfigurationSection("topNRewards"))) {
                boss.addReward(reward);
            }
            bosses.add(boss);
        }

        return bosses;
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

    private Map<BossSide, String> loadNames(ConfigurationSection nameSection) {
        Map<BossSide,String> names = new HashMap<>();
        for(String key : nameSection.getKeys(false)) {
            BossSide side = BossSide.valueOf(key.toUpperCase());
            names.put(side,nameSection.getString(key));
        }
        return names;
    }

    private Location loadLocation(World world ,ConfigurationSection location) {
        int x = location.getInt("x");
        int y = location.getInt("y");
        int z = location.getInt("z");

        return new Location(world,x,y,z);
    }

    @Override
    public List<MythicBoss> getBosses() {
        return bosses;
    }

    @Override
    public Location getSpawnLocation() {
        return spawnLocation;
    }

    @Override
    public MythicMob getBoss() {
        return boss;
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public void reload() {
        plugin.reloadConfig();
        loadConfig();
    }

    @Override
    public Map<BossSide, String> getNames() {
        return names;
    }
}
