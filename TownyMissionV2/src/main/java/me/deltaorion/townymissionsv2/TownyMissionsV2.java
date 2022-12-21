package me.deltaorion.townymissionsv2;

import com.google.common.base.Preconditions;
import me.deltaorion.townymissionsv2.command.MissionsCommand;
import me.deltaorion.townymissionsv2.configuration.ConfigurationException;
import me.deltaorion.townymissionsv2.configuration.MessageManager;
import me.deltaorion.townymissionsv2.configuration.StorageConfiguration;
import me.deltaorion.townymissionsv2.display.GovernmentDisplayListener;
import me.deltaorion.townymissionsv2.display.sound.SoundManager;
import me.deltaorion.townymissionsv2.mission.chat.ChatDefinition;
import me.deltaorion.townymissionsv2.mission.definition.GoalDefinition;
import me.deltaorion.townymissionsv2.mission.gather.GatherDefinition;
import me.deltaorion.townymissionsv2.mission.reward.type.RewardListener;
import me.deltaorion.townymissionsv2.player.PlayerManager;
import me.deltaorion.townymissionsv2.plugin.Dependency;
import me.deltaorion.townymissionsv2.plugin.DependencyManager;
import me.deltaorion.townymissionsv2.plugin.SimpleDependencyManager;
import me.deltaorion.townymissionsv2.storage.StorageImplementation;
import me.deltaorion.townymissionsv2.storage.StorageType;
import me.deltaorion.townymissionsv2.storage.sql.SqlStorage;
import me.deltaorion.townymissionsv2.test.MissionTest;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public final class TownyMissionsV2 extends JavaPlugin implements DependencyManager {

    private DependencyManager dependencyManager;
    private PlayerManager playerManager;
    private MissionManager missionManager;
    private HashMap<String,GoalDefinition> definitions;
    private MissionPool pool;

    private boolean storing = false;
    private StorageType storageType = null;
    private boolean usingPool = false;
    private StorageImplementation storage;


    @Override
    public void onEnable() {
        // Plugin startup logic
        this.dependencyManager = new SimpleDependencyManager(this);
        this.playerManager = new PlayerManager(this);
        this.missionManager = new MissionManager();
        this.definitions = new HashMap<>();
        
        printASCIIArt();
        reloadMessages();
        reloadSounds();
        registerDependencies();
        registerCommands();
        registerDefinitions();
        registerListeners();
        init();

        Bukkit.getLogger().info("==============================================================");

    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new GovernmentDisplayListener(this),this);
        getServer().getPluginManager().registerEvents(new RewardListener(this),this);
    }

    public void reloadMessages() {
        MessageManager.loadMessages(this);
    }

    public void reloadSounds() {
        SoundManager.loadSounds(this);
    }

    public void reloadPool() {
        try {
            if(pool!=null)
                if(pool.running())
                     pool.stop();

            StorageConfiguration configuration = new StorageConfiguration(this,"pool");
            this.pool = MissionPool.deserialize(configuration.getConfig(),this);

        } catch (ConfigurationException e){
            getLogger().severe("-----------------------");
            getLogger().severe("Configuration Error");
            getLogger().severe(e.getMessage());
            getLogger().severe("------------------------");
        }
    }

    private void init() {

        for(GoalDefinition definition : definitions.values()) {
            definition.onLoad();
        }

        loadConfig();
        reloadPool();
        loadStorage();
        startPool();
    }

    public void loadConfig() {
        StorageConfiguration configuration = new StorageConfiguration(this,"config.yml");

        try {
            storing = configuration.getConfig().getBoolean("use-storage",false);
            try {
                storageType = StorageType.valueOf(configuration.getConfig().getString("storage-type").toUpperCase());
            } catch (NullPointerException | IllegalArgumentException e) {
                throw new ConfigurationException("storage-type",configuration.getConfig().getString("storage-type"),"Unknown or Missing Storage Type");
            }
            usingPool = configuration.getConfig().getBoolean("start-pool",false);
        } catch (ConfigurationException e) {
            Bukkit.getLogger().severe("-----------------");
            Bukkit.getLogger().severe(e.getMessage());
            Bukkit.getLogger().severe("------------------");
        }

        if(storageType==null) {
            storing = false;
        }
        Bukkit.getLogger().info("Using Storage: "+storing);
        if(storageType != null) {
            Bukkit.getLogger().info("Storage Implementation: " + storageType.getIdentifier());
        }
        Bukkit.getLogger().info("Using Pool: "+usingPool);
    }

    private void loadStorage() {

        if(storing) {
            storage = new SqlStorage(this, this.storageType);
            storage.init();
        }
    }

    private void startPool() {
        if(usingPool) {
            pool.start();
        }
    }

    private void registerDefinitions() {
        addDefinition(new ChatDefinition(this));
        addDefinition(new GatherDefinition(this));
    }

    private void registerDependencies() {
        registerDependency("Towny",true);
    }

    private void registerCommands() {
        getCommand("testmissions").setExecutor(new MissionTest(this));
        getCommand("missions").setExecutor(new MissionsCommand(this));
    }

    private void printASCIIArt() {
        Bukkit.getLogger().info("==================== Towny Missions =======================");
    }


    @Override
    public void onDisable() {
        for(GoalDefinition definition : definitions.values()) {
            definition.onDisable();
        }

        if(storing && storage!=null) {
            storage.shutdown();
        }
    }

    @Override
    public @Nullable Dependency getDependency(String name) {
        return dependencyManager.getDependency(name);
    }

    @Override
    public boolean hasDependency(String name) {
        return dependencyManager.hasDependency(name);
    }

    @Override
    public Set<String> getDependencies() {
        return dependencyManager.getDependencies();
    }

    @Override
    public void registerDependency(String name, boolean required) {
        dependencyManager.registerDependency(name,required);
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public MissionManager getMissionManager() {
        return missionManager;
    }

    private void addDefinition(GoalDefinition definition) {
        Preconditions.checkNotNull(definition);
        this.definitions.put(definition.getName().toLowerCase(),definition);
    }

    public void registerDefinition(GoalDefinition definition) {
        addDefinition(definition);
        definition.onLoad();
    }


    public GoalDefinition getDefinition(String name) {
        return definitions.get(name.toLowerCase());
    }

    public GoalDefinition getDefinition(Definition definition) {
        return getDefinition(definition.getName());
    }

    public Collection<GoalDefinition> getDefinitions() {
        return definitions.values();
    }

    public MissionPool getPool() {
        return pool;
    }

    public StorageImplementation getStorage() {
        return storage;
    }
}
