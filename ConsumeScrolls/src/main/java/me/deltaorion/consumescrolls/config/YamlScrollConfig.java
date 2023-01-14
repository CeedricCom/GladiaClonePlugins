package me.deltaorion.consumescrolls.config;

import me.deltaorion.consumescrolls.Rarity;
import me.deltaorion.consumescrolls.ScrollDefinition;
import me.deltaorion.consumescrolls.reward.CommandReward;
import me.deltaorion.consumescrolls.reward.ItemReward;
import me.deltaorion.consumescrolls.reward.RewardType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class YamlScrollConfig implements ScrollConfig {

    private final File configFile;
    private List<String> toolTip;
    private String chatRewardMessage;
    private List<ScrollDefinition> scrolls;
    private boolean showTitle;
    private Map<Rarity,Integer> percentages;

    public YamlScrollConfig(File file, Plugin plugin) throws ConfigurationException {
        this.configFile = file;
        if(!configFile.exists()) {
            try {
                configFile.createNewFile();
                Files.copy(plugin.getResource("config.yml"),
                        configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new ConfigurationException(e.getMessage());
            }
        }
        reload();
    }

    @Override
    public void reload() throws ConfigurationException {
        toolTip = new ArrayList<>();
        scrolls = new ArrayList<>();
        percentages = new HashMap<>();
        if(!configFile.exists())
            throw new ConfigurationException("Config file '"+configFile.getName()+"' does not exist");

        Configuration config = YamlConfiguration.loadConfiguration(configFile);
        loadToolTip(config);
        loadShowTitle(config);
        loadRewardMessage(config);
        loadPercentages(config);
        loadScrolls(config);
    }

    private void loadScrolls(Configuration config) throws ConfigurationException {
        String key = "scrolls";
        if(!config.contains(key))
            throw new ConfigurationException("Missing '"+key+"' section");

        if(!config.isConfigurationSection(key))
            throw new ConfigurationException(key+" is not a config section");

        ConfigurationSection section = config.getConfigurationSection(key);
        for(String name : section.getKeys(false)) {
            ConfigurationSection defintionSect = section.getConfigurationSection(name);
            loadScroll(name,defintionSect);
        }
    }

    private void loadScroll(String name ,ConfigurationSection defintionSect) throws ConfigurationException {
        if(!defintionSect.contains("item"))
            throw new ConfigurationException("missing item for "+name);

        Material material = Material.valueOf(defintionSect.getString("item").toUpperCase(Locale.ROOT));

        if(!defintionSect.contains("rarity"))
            throw new ConfigurationException("missing rarity for "+name);

        Rarity rarity = Rarity.valueOf(defintionSect.getString("rarity").toUpperCase(Locale.ROOT));
        if(!defintionSect.contains("min-goal"))
            throw new ConfigurationException("missing min-goal for "+name);

        int minGoal = defintionSect.getInt("min-goal");

        if(!defintionSect.contains("max-goal"))
            throw new ConfigurationException("missing max-goal for "+name);

        int maxGoal = defintionSect.getInt("max-goal");

        ScrollDefinition definition = new ScrollDefinition(name,material,rarity,minGoal,maxGoal);
        scrolls.add(definition);

        if(!defintionSect.contains("rewards"))
            return;

        ConfigurationSection rewardSect = defintionSect.getConfigurationSection("rewards");
        loadRewards(definition,rewardSect);
    }

    private void loadRewards(ScrollDefinition definition, ConfigurationSection rewardSect) {
        for(String key : rewardSect.getKeys(false)) {
            ConfigurationSection reward = rewardSect.getConfigurationSection(key);
            RewardType type = RewardType.valueOf(reward.getString("type").toUpperCase(Locale.ROOT));
            switch (type) {
                case ITEM -> loadItem(definition,reward);
                case COMMAND -> loadCommand(definition,reward);
            }
        }
    }

    private void loadCommand(ScrollDefinition definition, ConfigurationSection reward) {
        String command = reward.getString("command");
        String name = reward.getString("name");
        definition.addReward(new CommandReward(command, name));
    }

    private void loadItem(ScrollDefinition definition, ConfigurationSection reward) {
        ItemStack itemStack = reward.getItemStack("itemstack");
        int amount = reward.getInt("amount");
        String name = reward.getString("name");
        if(name==null)
            name = "";
        definition.addReward(new ItemReward(ChatColor.translateAlternateColorCodes('&',name), itemStack,amount));
    }

    private void loadPercentages(Configuration config) throws ConfigurationException {
        String key = "percentages";

        if(!config.contains(key))
            throw new ConfigurationException("Missing '"+key+"' section");

        if(!config.isConfigurationSection(key))
            throw new ConfigurationException(key+" is not a config section");

        try {
            ConfigurationSection percentSection = config.getConfigurationSection(key);
            for (String rarity : percentSection.getKeys(false)) {
                Rarity R = Rarity.valueOf(rarity.toUpperCase(Locale.ROOT));
                if(!percentSection.isInt(rarity))
                    throw new ConfigurationException("Percentage not an integer");

                R.setPercentage(percentSection.getInt(rarity));
            }
        } catch (IllegalArgumentException e) {
            throw new ConfigurationException("unknown rarity");
        }
    }

    private void loadShowTitle(Configuration config) throws ConfigurationException {
        String key = "show-title";
        if(!config.contains(key))
            throw new ConfigurationException("Missing '"+key+"' section");

        if(!config.isBoolean(key))
            throw new ConfigurationException(key+" is not a boolean");

        showTitle = config.getBoolean(key);
    }

    private void loadToolTip(Configuration config) throws ConfigurationException {
        String key = "tool-tip";
        if(!config.contains(key))
            throw new ConfigurationException("Missing '"+key+"' section");

        this.toolTip.addAll(config.getStringList("tool-tip"));
    }

    private void loadRewardMessage(Configuration config) throws ConfigurationException {
        String key = "chat-reward-message";
        if(!config.contains(key))
            throw new ConfigurationException("Missing '"+key+"' section");

        this.chatRewardMessage = config.getString(key);
    }

    @Override
    public List<String> getToolTip() {
        return toolTip;
    }

    @Override
    public boolean showTitle() {
        return showTitle;
    }

    @Override
    public String getChatRewardMessage() {
        return chatRewardMessage;
    }

    @Override
    public List<ScrollDefinition> getScrolls() {
        return scrolls;
    }

    @Override
    public Map<Rarity, Integer> getPercentages() {
        return percentages;
    }
}
