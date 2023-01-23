package me.deltaorion.siegestats;

import me.deltaorion.bukkit.plugin.plugin.BukkitPlugin;
import me.deltaorion.siegestats.controller.command.SiegeStatCommand;
import me.deltaorion.siegestats.controller.listener.PlayerKillListener;
import me.deltaorion.siegestats.controller.listener.SiegeWarListener;
import me.deltaorion.siegestats.model.SiegeRepository;
import me.deltaorion.siegestats.model.YamlSiegeRepository;
import me.deltaorion.siegestats.service.PersistenceManager;
import me.deltaorion.siegestats.service.SiegeService;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class SiegeStatsPlugin extends BukkitPlugin {

    private SiegeService siegeManager;
    private PersistenceManager persistenceManager;

    @Override
    public void onPluginEnable() {
        // Plugin startup logic
        registerDependency("Towny",true);
        registerDependency("SiegeWar",true);

        this.siegeManager = new SiegeService();
        try {
            this.persistenceManager = new PersistenceManager(getRepository(),siegeManager);
        } catch (IOException e) {
            Bukkit.getLogger().severe("Unable to load database");
            onDisable();
            return;
        }

        registerCommand(new SiegeStatCommand(this),"siegestats");
        getServer().getPluginManager().registerEvents(new PlayerKillListener(siegeManager),this);
        getServer().getPluginManager().registerEvents(new SiegeWarListener(siegeManager),this);

        Bukkit.getLogger().info("Loading all data");
        persistenceManager.loadAll();
    }

    private SiegeRepository getRepository() throws IOException {
        File file = new File(getDataFolder(),"database");
        if(!file.exists())
            file.mkdir();

        return new YamlSiegeRepository(file);
    }

    @Override
    public void onPluginDisable() {
        Bukkit.getLogger().info("Saving all data");
        persistenceManager.saveAll();
    }

    public SiegeService getSiegeManager() {
        return siegeManager;
    }

    public PersistenceManager getPersistenceManager() {
        return persistenceManager;
    }

    public File getReportDir() {
        return new File(getDataFolder(),"reports");
    }
}
