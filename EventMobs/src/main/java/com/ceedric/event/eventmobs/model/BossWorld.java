package com.ceedric.event.eventmobs.model;

import com.ceedric.event.eventmobs.model.participant.MythicParticipant;
import com.ceedric.event.eventmobs.model.participant.OtherParticipant;
import com.ceedric.event.eventmobs.model.participant.Participant;
import com.ceedric.event.eventmobs.model.participant.PlayerParticipant;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class BossWorld {
    private final Map<String,MythicBoss> bosses;
    private final List<MythicKill> kills;
    private final Map<UUID, Participant> participants;
    private World world;
    private long eventStart;

    public BossWorld(World world) {
        this.world = world;
        bosses = new HashMap<>();
        this.kills = new ArrayList<>();
        this.participants = new HashMap<>();
    }

    public Participant getParticipant(UUID uuid) {
        return participants.get(uuid);
    }

    public void addParticipant(Participant participant) {
        this.participants.put(participant.getUniqueId(),participant);
    }

    public PlayerParticipant getPlayer(UUID uniqueId) {
        Participant participant = getParticipant(uniqueId);
        if(participant instanceof PlayerParticipant)
            return (PlayerParticipant) participant;

        return null;
    }

    @NotNull
    public Participant getOrMakeParticipant(Entity entity, String cause) {
        Participant participant = null;
        if (entity instanceof Player player) {
            participant = participants.get(player.getUniqueId());
            if (participant == null) {
                participant = new PlayerParticipant(player);
                addParticipant(participant);
            }
        } else {
            MythicMob mob = null;
            if(entity!=null) {
                ActiveMob activeMob =MythicBukkit.inst().getMobManager().getMythicMobInstance(entity);
                if(activeMob!=null)
                    mob = activeMob.getType();
            }

            if(mob!=null) {
                participant = participants.get(MythicParticipant.getId(mob));
                if (participant == null) {
                    participant = new MythicParticipant(mob);
                    addParticipant(participant);
                }
            } else {
                participant = participants.get(OtherParticipant.getId(cause));
                if(participant==null) {
                    participant = new OtherParticipant(cause);
                    addParticipant(participant);
                }
            }
        }
        return participant;
    }

    public Collection<Participant> getParticipants() {
        return Collections.unmodifiableCollection(participants.values());
    }

    public void addKill(MythicKill kill) {
        this.kills.add(kill);
    }

    public Collection<MythicKill> getKills() {
        return Collections.unmodifiableList(kills);
    }

    public void addBoss(MythicBoss boss) {
        this.bosses.put(boss.getBossName(),boss);
    }

    public MythicBoss getBoss(String bossName) {
        return bosses.get(bossName);
    }

    public Collection<MythicBoss> getBosses() {
        return bosses.values();
    }

    public void setEventStart() {
        this.eventStart = System.currentTimeMillis();
    }

    public long getEventStart() {
        return eventStart;
    }

    public Participant getParticipantByName(String name) {
        for(Participant participant : getParticipants()) {
            if(ChatColor.stripColor(participant.getName()).equalsIgnoreCase(name))
                return participant;
        }

        return null;
    }

    public World getWorld() {
        return world;
    }

    public void clear() {
        kills.clear();
        participants.clear();
    }

    public void clearBosses() {
        this.bosses.clear();
    }

    public BossWorld clone() {
        BossWorld world = new BossWorld(getWorld());
        world.eventStart = getEventStart();
        for(MythicBoss boss : getBosses()) {
            world.addBoss(boss);
        }

        for(Participant participant : getParticipants()) {
            world.addParticipant(participant.clone());
        }

        for(MythicKill kill : getKills()) {
            world.addKill(new MythicKill(kill.getLocation(),
                    world.getParticipant(kill.getKiller().getUniqueId()),
                    world.getParticipant(kill.getVictim().getUniqueId()),
                    kill.getDeathSide()));
        }

        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }
}
