package me.deltaorion.siegecommandblacklist;

import org.bukkit.configuration.Configuration;

import java.util.List;

public class YamlBlackListConfig implements BlackListConfig {

    private Configuration configuration;
    private final SiegeCommandBlacklist plugin;
    private final static String BLACKLIST_KEY = "blacklist";

    public YamlBlackListConfig(SiegeCommandBlacklist plugin) {
        this.configuration = plugin.reload();
        this.plugin = plugin;
    }

    @Override
    public List<String> getBlackList() {
        return configuration.getStringList(BLACKLIST_KEY);
    }

    @Override
    public void reload() {
        configuration = plugin.reload();
    }

}
