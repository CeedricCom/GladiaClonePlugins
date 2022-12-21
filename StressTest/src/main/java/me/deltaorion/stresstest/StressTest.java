package me.deltaorion.stresstest;

import org.bukkit.plugin.java.JavaPlugin;

public final class StressTest extends JavaPlugin {

    private static StressTest instance;
    private Teleporter teleporter;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        teleporter = new Teleporter();
        getCommand("StressTest").setExecutor(new StressTestCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static StressTest getInstance() {
        return instance;
    }

    public Teleporter getTeleporter() {
        return teleporter;
    }
}
