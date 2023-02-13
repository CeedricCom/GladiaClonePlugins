package me.deltaorion.ilegalworld;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class IlegalWorld extends JavaPlugin {

    private final List<String> illegalWorlds = new ArrayList<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        reloadConfiguration();
        Listeners controller = new Listeners(this, Bukkit.getWorld("world"));
        getCommand("illegalworld").setExecutor(controller);
        getServer().getPluginManager().registerEvents(controller,this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public List<String> getIllegalWorlds() {
        return illegalWorlds;
    }


    public void reloadConfiguration() {
        illegalWorlds.clear();
        reloadConfig();
        illegalWorlds.addAll(getConfig().getStringList("worlds"));
    }
}
