package me.deltaorion.eventcommands.config;

import me.deltaorion.eventcommands.EventCommandsPlugin;
import me.deltaorion.eventcommands.SpawnTemplate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class YamlSpawnConfig implements SpawnConfig {

    private final List<SpawnTemplate> spawns;
    private final EventCommandsPlugin plugin;

    public YamlSpawnConfig(EventCommandsPlugin plugin) {
        this.plugin = plugin;
        spawns = new ArrayList<>();
        reload();
    }

    @Override
    public Collection<SpawnTemplate> getSpawns() {
        return Collections.unmodifiableList(spawns);
    }

    @Override
    public void reload() {
        plugin.reloadConfig();
        loadConfig();
    }

    private void loadConfig() {
        FileConfiguration config = plugin.getConfig();
        ConfigurationSection spawnSection = config.getConfigurationSection("spawns");
        for(String key : spawnSection.getKeys(false)) {
            this.spawns.add(getSpawn(spawnSection.getConfigurationSection(key)));
        }
    }

    private SpawnTemplate getSpawn(ConfigurationSection spawnSection) {
        String displayName = spawnSection.getString("display-name");
        String commandName = spawnSection.getString("command-name");
        ConfigurationSection spawnLocation = spawnSection.getConfigurationSection("location");
        Vector location = getLocation(spawnLocation);
        double pitch  =spawnLocation.getDouble("pitch");
        double yaw = spawnLocation.getDouble("yaw");
        String worldName = spawnSection.getString("world");

        return new SpawnTemplate(worldName,location.getX(),location.getY(),location.getZ(), pitch, yaw, commandName,displayName);
    }

    private Vector getLocation(ConfigurationSection location) {
        double x = location.getDouble("x");
        double y = location.getDouble("y");
        double z = location.getDouble("z");
        return new Vector(x,y,z);
    }
}
