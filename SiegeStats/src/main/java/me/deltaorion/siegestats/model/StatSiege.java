package me.deltaorion.siegestats.model;

import com.gmail.goosius.siegewar.SiegeController;
import com.gmail.goosius.siegewar.enums.SiegeSide;
import com.gmail.goosius.siegewar.objects.Siege;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Government;
import me.deltaorion.siegestats.StringUtil;
import me.deltaorion.siegestats.model.killer.EntityParticipant;
import me.deltaorion.siegestats.model.killer.OtherParticipant;
import me.deltaorion.siegestats.model.killer.Participant;
import me.deltaorion.siegestats.model.killer.PlayerParticipant;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class StatSiege {

    private final SiegeTown beseiged;
    private final UUID invader;
    private final String invaderName;
    private final String defenderNationName;
    private final List<SiegeKill> kills;
    private final Map<UUID, Participant> participants;
    private SiegeSide victor;
    private final long startTime;

    public StatSiege(SiegeTown beseiged, UUID invaderId, String invaderName, long startTime, String defenderNationName) {
        this.invader = invaderId;
        this.beseiged = beseiged;
        this.invaderName = invaderName;
        this.startTime = startTime;
        this.defenderNationName = defenderNationName;
        this.kills = new ArrayList<>();
        this.participants = new HashMap<>();
        this.victor = SiegeSide.NOBODY;
    }

    public List<SiegeKill> getKills() {
        return Collections.unmodifiableList(kills);
    }

    public Collection<Participant> getParticipants() {
        return Collections.unmodifiableCollection(participants.values());
    }

    public Participant getByName(String name) {
        for(Participant participant : participants.values()) {
            if(participant.getName().equalsIgnoreCase(name))
                return participant;
        }

        return null;
    }

    public Participant get(UUID uuid) {
        return participants.get(uuid);
    }

    public Participant getOrMake(Entity attacker, String reason) {
        Participant participant = null;
        if(attacker instanceof Player) {
            participant = new PlayerParticipant((Player) attacker);
        } else if (attacker != null) {
            participant = new EntityParticipant(attacker.getType());
        } else {
            participant = new OtherParticipant(StringUtil.getFriendlyName(reason));
        }

        Participant existing = participants.get(participant.getUniqueId());
        if(existing==null) {
            addParticipant(participant);
            return participant;
        } else {
            return existing;
        }
    }

    public Participant getOrMake(Player player) {
        Participant participant = participants.get(player.getUniqueId());
        if(participant==null) {
            participant = new PlayerParticipant(player);
            addParticipant(participant);
        }

        return participant;
    }

    public void addKill(SiegeKill kill) {
        this.kills.add(kill);
    }

    public void addParticipant(Participant participant) {
        this.participants.put(participant.getUniqueId(),participant);
    }

    public long getStartTime() {
        return startTime;
    }

    public SiegeTown getBesieged() {
        return beseiged;
    }

    @Nullable
    public Government getInvader() {
        return TownyUniverse.getInstance().getNation(invader);
    }

    public UUID getInvaderId() {
        return invader;
    }

    @NotNull
    public String getInvaderName() {
        return invaderName;
    }

    public Siege getSiege() {
        if(getBesieged()==null)
            return null;

        return SiegeController.getSiege(getBesieged().asTown());
    }

    public void setVictor(SiegeSide victor) {
        this.victor = victor;
    }

    public SiegeSide getVictor() {
        return victor;
    }

    public String getDefenderNationName() {
        return defenderNationName;
    }

    @Override
    public StatSiege clone() {
        StatSiege siege = new StatSiege(beseiged,invader,invaderName,startTime,defenderNationName);
        siege.victor = victor;
        for(Participant participant : getParticipants()) {
            siege.addParticipant(participant.clone());
        }

        for(SiegeKill kill : getKills()) {
            siege.addKill(new SiegeKill(siege.get(kill.getKiller().getUniqueId()),kill.getDeathSide(),siege.get(kill.getVictim().getUniqueId()),kill.getLocation(),kill.getTime()));
        }

        return siege;
    }
}
