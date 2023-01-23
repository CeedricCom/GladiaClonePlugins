package me.deltaorion.siegestats.model;

import com.gmail.goosius.siegewar.enums.SiegeSide;
import me.deltaorion.siegestats.model.killer.EntityParticipant;
import me.deltaorion.siegestats.model.killer.OtherParticipant;
import me.deltaorion.siegestats.model.killer.Participant;
import me.deltaorion.siegestats.model.killer.PlayerParticipant;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class YamlSiegeRepository implements SiegeRepository {

    private final File directory;

    public YamlSiegeRepository(File directory) {
        this.directory = directory;
    }

    @Override
    public void createTown(SiegeTown town) {
        File file = new File(directory,town.getUniqueId().toString());
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


        FileConfiguration siegeTown = YamlConfiguration.loadConfiguration(file);
        siegeTown.set("name",town.getLastName());
        ConfigurationSection siegesSection = siegeTown.getConfigurationSection("sieges");
        if(siegesSection==null)
            siegesSection = siegeTown.createSection("sieges");

        int ordinal = 0;
        for(StatSiege siege : town.getSieges()) {
            ConfigurationSection siegeSection = siegesSection.getConfigurationSection(String.valueOf(ordinal));
            if (siegeSection == null)
                siegeSection = siegesSection.createSection(String.valueOf(ordinal));

            saveSiege(siegeSection,siege);
            ordinal++;
        }

        try {
            siegeTown.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SiegeTown getTown(UUID townId) {
        File file = new File(directory,townId.toString());
        if(!file.exists())
            return null;

        return loadFromFile(townId,file);
    }

    private SiegeTown loadFromFile(UUID townId, File file) {
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        String name = configuration.getString("name");
        SiegeTown town = new SiegeTown(townId,name);
        ConfigurationSection sieges = configuration.getConfigurationSection("sieges");
        for(String key : sieges.getKeys(false)) {
            ConfigurationSection siege = sieges.getConfigurationSection(key);
            town.addSiege(loadSiege(town,siege));
        }

        return town;
    }

    private StatSiege loadSiege(SiegeTown town, ConfigurationSection configuration) {
        long time = configuration.getLong("time");
        UUID attacker = UUID.fromString(configuration.getString("attacker"));
        String attackerName = configuration.getString("attacker-name");
        String defenderName = configuration.getString("defender-name");
        SiegeSide victor = SiegeSide.valueOf(configuration.getString("victor"));
        StatSiege siege = new StatSiege(town,attacker,attackerName,time,defenderName);
        siege.setVictor(victor);
        ConfigurationSection particpantSection = configuration.getConfigurationSection("participants");
        for(String key : particpantSection.getKeys(false)) {
            siege.addParticipant(loadParticipant(siege,particpantSection.getConfigurationSection(key)));
        }

        ConfigurationSection killSection = configuration.getConfigurationSection("kills");
        for(String key : killSection.getKeys(false)) {
            siege.addKill(loadKill(siege,killSection.getConfigurationSection(key)));
        }

        return siege;
    }

    private SiegeKill loadKill(StatSiege siege, ConfigurationSection killSection) {
        World world = Bukkit.getWorld(killSection.getString("world"));
        UUID killer = UUID.fromString(killSection.getString("killer"));
        UUID victim = UUID.fromString(killSection.getString("victim"));
        long time  = killSection.getLong("time");
        SiegeSide deathSide = SiegeSide.valueOf(killSection.getString("side"));
        double x = killSection.getDouble("x");
        double y = killSection.getDouble("y");
        double z = killSection.getDouble("z");

        Location location = new Location(world,x,y,z);
        Participant killerParticipant = siege.get(killer);
        Participant victimParticipant = siege.get(victim);

        return new SiegeKill(killerParticipant,deathSide,victimParticipant,location,time);
    }

    private Participant loadParticipant(StatSiege siege, ConfigurationSection participantSection) {
        String type = participantSection.getString("type");
        double damage = participantSection.getDouble("damage");
        Participant participant = null;
        switch (type) {
            case "player" -> {
                UUID uniqueId = UUID.fromString(participantSection.getString("uniqueId"));
                participant = new PlayerParticipant(uniqueId);
            } case "entity" -> {
                EntityType entityType = EntityType.valueOf(participantSection.getString("entity"));
                participant =  new EntityParticipant(entityType);
            } case "other" -> {
                participant = new  OtherParticipant(participantSection.getString("name"));
            }
        }

        participant.addDamage(damage);
        return participant;
    }

    @Override
    public void updateTown(SiegeTown town) {
        createTown(town);
    }

    @Override
    public Collection<SiegeTown> getAllTowns() {
        if(!directory.exists())
            directory.mkdir();

        List<SiegeTown> towns = new ArrayList<>();
        for(File file : directory.listFiles()) {
            String name = file.getName();
            towns.add(loadFromFile(UUID.fromString(name),file));
        }

        return towns;
    }

    private void saveSiege(ConfigurationSection siegeSection, StatSiege siege) {
        siegeSection.set("time", siege.getStartTime());
        siegeSection.set("attacker", siege.getInvaderId().toString());
        siegeSection.set("attacker-name", siege.getInvaderName());
        siegeSection.set("defender-name",siege.getDefenderNationName());
        siegeSection.set("victor",siege.getVictor().name());

        ConfigurationSection killsSection = siegeSection.createSection("kills");
        int count = 0;
        for (SiegeKill kill : siege.getKills()) {
            ConfigurationSection killSection = killsSection.createSection(String.valueOf(count));
            saveKill(killSection, kill);
            count++;
        }

        ConfigurationSection participantsSection = siegeSection.createSection("participants");
        for (Participant participant : siege.getParticipants()) {
            ConfigurationSection participantSection = participantsSection.createSection(String.valueOf(participant.getUniqueId()));
            saveParticipant(participantSection, participant);
        }

    }

    private void saveKill(ConfigurationSection killSection, SiegeKill kill) {
        killSection.set("world",kill.getLocation().getWorld().getName());
        killSection.set("killer",kill.getKiller().getUniqueId().toString());
        killSection.set("victim",kill.getVictim().getUniqueId().toString());
        killSection.set("time",kill.getTime());
        killSection.set("side",kill.getDeathSide().name());
        killSection.set("x",kill.getLocation().getX());
        killSection.set("y",kill.getLocation().getY());
        killSection.set("z",kill.getLocation().getZ());
    }

    private void saveParticipant(ConfigurationSection participantSection ,Participant participant) {
        if(participant instanceof PlayerParticipant) {
            participantSection.set("type","player");
            participantSection.set("uniqueId",participant.getUniqueId().toString());
        } else if(participant instanceof EntityParticipant entityParticipant) {
            participantSection.set("type","entity");
            participantSection.set("entity",entityParticipant.getType().name());
        } else if(participant instanceof OtherParticipant otherParticipant) {
            participantSection.set("type","other");
            participantSection.set("name",otherParticipant.getRawName());
        }

        participantSection.set("damage",participant.getDamage());
    }

}
