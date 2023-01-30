package me.deltaorion.townymissionsv2.player;

import com.google.common.base.Preconditions;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Government;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.utils.MetaDataUtil;
import me.deltaorion.townymissionsv2.TownyMissionsV2;
import me.deltaorion.townymissionsv2.bearer.GovernmentMissionBearer;
import me.deltaorion.townymissionsv2.bearer.MissionBearer;
import me.deltaorion.townymissionsv2.display.MinecraftSound;
import me.deltaorion.townymissionsv2.util.TownyUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MissionPlayer {

    private final UUID uuid;
    private final Plugin plugin;
    private AutoContribute autoContribute = new AutoContribute(this);
    private ContributeType contributePriority;

    private final static String TOWN_BEARER_SOURCE_ID_KEY = "TownyMissionsV2_Town_Bearer_Source_Name";
    private final static String TOWN_BEARER_SOURCE_TIME_KEY = "TownyMissionsV2_Town_Bearer_Source_Time";

    private final static String NATION_BEARER_SOURCE_ID_KEY = "TownyMissionsV2_Nation_Bearer_Source_Name";
    private final static String NATION_BEARER_SOURCE_TIME_KEY = "TownyMissionsV2_Nation_Bearer_Source_Time";

    public MissionPlayer(UUID uuid, Plugin plugin) {

        Preconditions.checkNotNull(uuid);
        Preconditions.checkNotNull(plugin);

        this.uuid = uuid;
        this.plugin = plugin;
        this.contributePriority = ContributeType.TOWN;
    }

    public Player getPlayer() {
        return plugin.getServer().getPlayer(uuid);
    }

    public boolean isOnline() {
        return getPlayer().isOnline();
    }

    public Town getTown() {
        try {
            return getResident().getTown();
        } catch (NotRegisteredException e) {
            return null;
        }
    }

    public Nation getNation() {
        try {
            if(getTown()==null)
                return null;

            return getTown().getNation();
        } catch (NotRegisteredException e) {
            return null;
        }
    }

    public List<Government> getGovernmentsApartOf() {
        List<Government> governments = new ArrayList<>();
        if(getNation()!=null) {
            governments.add(getNation());
        }

        if(getTown()!=null) {
            governments.add(getTown());
        }

        return governments;
    }

    public List<UUID> getGovernmentIDs() {
        List<UUID> governments = new ArrayList<>();
        if(getNation()!=null) {
            governments.add(getNation().getUUID());
        }

        if(getTown()!=null) {
            governments.add(getTown().getUUID());
        }

        return governments;
    }

    public Resident getResident() {
        return TownyUniverse.getInstance().getResident(uuid);
    }

    public void playSound(MinecraftSound sound) {
        sound.playSound(getPlayer());
    }

    public UUID getUuid() {
        return uuid;
    }

    public AutoContribute getAutoContribute() {
        return autoContribute;
    }

    public ContributeType getContributePriority() {
        return contributePriority;
    }

    public void setContributePriority(ContributeType contributePriority) {
        Preconditions.checkNotNull(contributePriority);
        this.contributePriority = contributePriority;
    }

    public Government getGovernment(ContributeType type) {

        Preconditions.checkNotNull(type);

        if(type.equals(ContributeType.NATION)) {
            return getNation();
        } else {
            return getTown();
        }
    }

    public void setContributionLock(MissionBearer bearer) {
        if(bearer instanceof GovernmentMissionBearer govBearer) {
            Player bukkitPlayer = getPlayer();
            Duration contributionLockTime = TownyMissionsV2.getInstance().getContributionLockTime();
            if(bukkitPlayer==null)
                return;

            String sourceIdKey = "";
            String sourceTimeKey = "";

            if(govBearer.isTown()) {
                sourceIdKey = TOWN_BEARER_SOURCE_ID_KEY;
                sourceTimeKey = TOWN_BEARER_SOURCE_TIME_KEY;
            } else {
                sourceIdKey = NATION_BEARER_SOURCE_ID_KEY;
                sourceTimeKey = NATION_BEARER_SOURCE_TIME_KEY;
            }

            List<MetadataValue> sourceIdValues = bukkitPlayer.getMetadata(sourceIdKey);
            List<MetadataValue> sourceTimeValues = bukkitPlayer.getMetadata(sourceTimeKey);

            if(sourceIdValues.size() > 0 || sourceTimeValues.size() > 0) {
                long lastTime = sourceTimeValues.get(sourceTimeValues.size()-1).asLong();

                if(System.currentTimeMillis() - lastTime < contributionLockTime.toMillis())
                    return;
            }
            bukkitPlayer.setMetadata(sourceIdKey,new FixedMetadataValue(plugin, bearer.getUniqueID().toString()));
            bukkitPlayer.setMetadata(sourceTimeKey,new FixedMetadataValue(plugin, System.currentTimeMillis()));
        }
    }

    public long getContributionLockTime(MissionBearer bearer) {
        if(bearer instanceof GovernmentMissionBearer govBearer) {
            Duration contributionLockTime = TownyMissionsV2.getInstance().getContributionLockTime();
            Player bukkitPlayer = getPlayer();
            if(bukkitPlayer==null)
                return 0;

            List<MetadataValue> sourceIdValues = new ArrayList<>();
            List<MetadataValue> sourceTimeValues = new ArrayList<>();

            if(govBearer.isTown()) {
                sourceIdValues.addAll(bukkitPlayer.getMetadata(TOWN_BEARER_SOURCE_ID_KEY));
                sourceTimeValues.addAll(bukkitPlayer.getMetadata(TOWN_BEARER_SOURCE_TIME_KEY));
            } else {
                sourceIdValues.addAll(bukkitPlayer.getMetadata(NATION_BEARER_SOURCE_ID_KEY));
                sourceTimeValues.addAll(bukkitPlayer.getMetadata(NATION_BEARER_SOURCE_TIME_KEY));
            }

            if(sourceIdValues.size()==0 || sourceTimeValues.size()==0)
                return 0;

            UUID lastSource = UUID.fromString(sourceIdValues.get(sourceIdValues.size()-1).asString());
            long lastTime = sourceTimeValues.get(sourceTimeValues.size()-1).asLong();

            if(System.currentTimeMillis() - lastTime > contributionLockTime.toMillis())
                return 0;


            if(lastSource.equals(bearer.getUniqueID()))
                return 0;

            return lastTime + contributionLockTime.toMillis() - System.currentTimeMillis();
        }

        return 0;
    }
}
