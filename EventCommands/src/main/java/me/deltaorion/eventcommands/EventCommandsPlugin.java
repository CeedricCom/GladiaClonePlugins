package me.deltaorion.eventcommands;

import me.deltaorion.bukkit.plugin.plugin.BukkitPlugin;
import me.deltaorion.eventcommands.command.EventCommand;
import me.deltaorion.eventcommands.config.SpawnConfig;
import me.deltaorion.eventcommands.config.YamlSpawnConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class EventCommandsPlugin extends BukkitPlugin {

    private SpawnConfig config;
    private List<EventSpawn> spawns;
    private EventCommand command;

    @Override
    public void onPluginEnable() {
        // Plugin startup logic
        this.config = createConfig();
        spawns = new ArrayList<>();
        command = new EventCommand(this);
        registerCommand(command,"event");
        loadConfig();
    }

    public void loadConfig() {
        config.reload();
        spawns.clear();
        command.deregister();

        for(SpawnTemplate spawn : config.getSpawns()) {
            World world = Bukkit.getWorld(spawn.getWorldName());
            if(world==null) {
                Bukkit.getLogger().info("Loading World '"+spawn.getWorldName()+"'");
                WorldCreator creator = new WorldCreator(spawn.getWorldName());
                world = getServer().createWorld(creator);
            }

            if(world==null) {
                Bukkit.getLogger().severe("No world of name '"+spawn.getWorldName()+"' exists");
                continue;
            }

            Location location = new Location(world,spawn.getX(),spawn.getY(),spawn.getZ(),(float) spawn.getYaw(), (float) spawn.getPitch());
            EventSpawn s = new EventSpawn(location,spawn.getCommandName(),spawn.getDisplayName());
            spawns.add(s);
        }

        for(EventSpawn spawn : spawns) {
            command.register(spawn);
        }
    }

    public Collection<EventSpawn> getSpawns() {
        return Collections.unmodifiableList(spawns);
    }

    private SpawnConfig createConfig() {
        saveDefaultConfig();
        return new YamlSpawnConfig(this);
        /*
        TestSpawnConfig config = new TestSpawnConfig();
        config.addSpawn(new SpawnTemplate("world",100,100,100,
                "test",
                "&e&lTest"));

        config.addSpawn(new SpawnTemplate("invasion",100,100,100,
                "invasion",
                "&e&lInvasion"));

        return config;

         */
    }

    public SpawnConfig getIConfig() {
        return config;
    }

}
