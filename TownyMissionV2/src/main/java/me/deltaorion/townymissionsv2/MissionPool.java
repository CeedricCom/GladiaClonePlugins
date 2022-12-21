package me.deltaorion.townymissionsv2;

import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Government;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Town;
import me.deltaorion.townymissionsv2.bearer.MissionBearer;
import me.deltaorion.townymissionsv2.configuration.ConfigurationException;
import me.deltaorion.townymissionsv2.mission.Mission;
import me.deltaorion.townymissionsv2.mission.MissionGenerator;
import me.deltaorion.townymissionsv2.player.ContributeType;
import me.deltaorion.townymissionsv2.util.DurationParser;
import me.deltaorion.townymissionsv2.util.RandomHelper;
import me.deltaorion.townymissionsv2.util.TownyUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class MissionPool implements ConfigurationSerializable {

    private final static Duration DEFAULT_DURATION = Duration.of(10, ChronoUnit.MINUTES);

    private final List<MissionGenerator> pool;
    private final TownyMissionsV2 plugin;
    private final RandomHelper random;
    private final long tick;
    private BukkitRunnable runnable;

    private Iterator<Town> townIterator;
    private Iterator<Nation> nationIterator;

    public MissionPool(TownyMissionsV2 plugin, Duration tick, List<MissionGenerator> pool) {
        this.plugin = plugin;
        random = new RandomHelper();
        this.tick = convertTick(tick);
        this.pool = pool;
        this.townIterator = Collections.emptyIterator();
        this.nationIterator = Collections.emptyIterator();
    }

    private long convertTick(Duration tick) {
        if (tick.toMillis() < 50)
            throw new IllegalArgumentException("Runnable Cannot Tick faster than 1 tick per second '" + tick + "'");

        return tick.toMillis() / 50;
    }

    private Duration fromTick() {
        return Duration.of(tick * 50, ChronoUnit.MILLIS);
    }

    public void start() {
        runnable = generate();
        runnable.runTaskTimer(plugin,0L,1L);
    }

    public void stop() {
        runnable.cancel();
        runnable = null;
    }

    public void tick() {
        //loop through all governments, hand mission if possible
        processTown();
        processNation();
    }

    private void processNation() {
        List<MissionBearer> nations = new ArrayList<>();
        if(!nationIterator.hasNext()) {
            nationIterator = TownyUniverse.getInstance().getNations().iterator();
            if(!nationIterator.hasNext())
                return;
        }

        Nation nation = nationIterator.next();
        nations.add(plugin.getMissionManager().getMissionBearer(nation.getUUID()));

        process(nations, ContributeType.NATION);
    }

    private void processTown() {
        List<MissionBearer> towns = new ArrayList<>();
        if(!townIterator.hasNext()) {
            townIterator = TownyUniverse.getInstance().getTowns().iterator();
            if(!townIterator.hasNext())
                return;
        }

        Town town = townIterator.next();
        towns.add(plugin.getMissionManager().getMissionBearer(town.getUUID()));

        process(towns, ContributeType.TOWN);
    }

    public void process(List<MissionBearer> bearers, ContributeType type) {

        List<MissionGenerator> generators = getGeneratorsOfType(type);

        for (MissionBearer bearer : bearers) {
            addNewMission(bearer,generators);
        }
    }

    private void addNewMission(MissionBearer bearer,List<MissionGenerator> generators) {
        if(generators.size()==0)
            return;

        if(bearer.getPrimaryMission()!=null)
            return;

        if(bearer.onCooldown())
            return;

        final int rand = random.randomInt(0,generators.size()-1);
        Mission mission = generators.get(rand).getMission(bearer);
        bearer.addMission(mission);
    }

    private List<MissionGenerator> getGeneratorsOfType(ContributeType type) {
        List<MissionGenerator> generators = new ArrayList<>();
        for (MissionGenerator generator : getPool()) {
            if (generator.isOfType(type)) {
                generators.add(generator);
            }
        }
        return generators;
    }


    public boolean running() {
        return runnable != null;
    }

    public BukkitRunnable generate() {
        return new BukkitRunnable() {
            @Override
            public void run() {
                tick();
            }
        };
    }

    public String toString() {
        return "Tick: " + tick + " Running: " + running() + " pool: " + pool;
    }


    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("tick", DurationParser.print(fromTick()));
        Map<String, Object> poolMap = new LinkedHashMap<>();
        for (int i = 0; i < pool.size(); i++) {
            poolMap.put(String.valueOf(i), pool.get(i).serialize());
        }
        map.put("pool", poolMap);
        return map;
    }

    public static MissionPool deserialize(ConfigurationSection section, TownyMissionsV2 plugin) throws ConfigurationException {
        Duration tick = DEFAULT_DURATION;
        if (section.contains("tick")) {
            try {
                tick = DurationParser.parseDuration(section.getString("tick"));
            } catch (IllegalArgumentException e) {
                throw new ConfigurationException("tick", section.getString("tick"), "Cannot parse duration");
            }
        }

        if (!section.contains("pool"))
            throw new ConfigurationException(section, "pool");

        ConfigurationSection poolSection = section.getConfigurationSection("pool");

        List<MissionGenerator> generators = new ArrayList<>();

        for (String key : poolSection.getKeys(false)) {
            MissionGenerator generator = MissionGenerator.deserialize(poolSection.getConfigurationSection(key), plugin);
            generators.add(generator);
        }

        return new MissionPool(plugin, tick, generators);
    }

    public List<MissionGenerator> getPool() {
        return pool;
    }

    public MissionGenerator getGenerator(String name) {
        for (MissionGenerator generator : pool) {
            if (generator.getName().equalsIgnoreCase(name))
                return generator;
        }

        return null;
    }
}
