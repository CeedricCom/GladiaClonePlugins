package me.deltaorion.siegecommandblacklist;

import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class SiegeCommandBlacklist extends JavaPlugin {

    private BlackListConfig blackListConfig;

    @Override
    public void onEnable() {
        // Plugin startup logic
        blackListConfig = new YamlBlackListConfig(this);
        getServer().getPluginManager().registerEvents(new CommandListener(blackListConfig),this);
        getCommand("siegeblacklist").setExecutor(new BlacklistCommand(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public Configuration reload() {
        reloadConfig();
        return getConfig();
    }

    @NotNull
    public BlackListConfig getBlackListConfig() {
        return blackListConfig;
    }
}
