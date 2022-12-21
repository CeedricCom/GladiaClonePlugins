package me.deltaorion.townymissionsv2.player;

import com.google.common.base.Preconditions;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Government;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import me.deltaorion.townymissionsv2.display.MinecraftSound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MissionPlayer {

    private final UUID uuid;
    private final Plugin plugin;
    private AutoContribute autoContribute = new AutoContribute(this);
    private ContributeType contributePriority;

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
}
