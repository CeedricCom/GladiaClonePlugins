package com.ceedric.event.eventmobs.model;

import com.ceedric.event.eventmobs.model.participant.*;
import com.ceedric.event.eventmobs.model.reward.TopNReward;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class Event {

    private final String name;
    private final List<EventKill> kills;
    private final Map<UUID, Participant> participants;
    private final TreeSet<TopNReward> rewards;
    private World world;
    private long startTime;

    private String displayName;
    private String commandName;
    private Location spawnLocation;

    public boolean enabled = true;

    public Event(String name, World world) {
        this.name = name;
        this.world = world;
        this.kills = new ArrayList<>();
        this.participants = new HashMap<>();
        this.rewards = new TreeSet<>();
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
        } else if (entity != null){
            MythicMob mob = null;
            ActiveMob activeMob = MythicBukkit.inst().getMobManager().getMythicMobInstance(entity);
            if(activeMob!=null)
                mob = activeMob.getType();

            if(mob!=null) {
                participant = participants.get(MythicParticipant.getId(mob));
                if (participant == null) {
                    participant = new MythicParticipant(mob);
                    addParticipant(participant);
                }
            } else {
                participant = participants.get(EntityParticipant.getId(entity.getType()));
                if (participant == null) {
                    participant = new EntityParticipant(entity.getType());
                    addParticipant(participant);
                }
            }
        } else {
            participant = participants.get(OtherParticipant.getId(cause));
            if(participant==null) {
                participant = new OtherParticipant(cause);
                addParticipant(participant);
            }
        }
        return participant;
    }

    public Collection<Participant> getParticipants() {
        return Collections.unmodifiableCollection(participants.values());
    }

    public void addKill(EventKill kill) {
        this.kills.add(kill);
    }

    public Collection<EventKill> getKills() {
        return Collections.unmodifiableList(kills);
    }

    public void setEventStart(long eventStart) {
        this.startTime = eventStart;
    }

    public long getStartTime() {
        return startTime;
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

    public void setWorld(World world) {
        this.world = world;
    }

    public void clear() {
        kills.clear();
        participants.clear();
        this.rewards.clear();
        clearSpecific();
    }

    protected abstract void clearSpecific();

    public String getName() {
        return name;
    }

    public Event clone() {
        Event clone = cloneSpecific();
        clone.startTime = this.startTime;

        for(Participant participant : getParticipants()) {
            clone.addParticipant(participant.clone());
        }

        for(EventKill kill : getKills()) {
            clone.addKill(new EventKill(kill.getTime(),
                    kill.getLocation(),
                    clone.getParticipant(kill.getKiller().getUniqueId()),
                    clone.getParticipant(kill.getVictim().getUniqueId()),
                    kill.getDeathSide()));
        }

        return clone;
    }

    public Collection<TopNReward> getRewards() {
        return Collections.unmodifiableSet(rewards);
    }

    public void addReward(TopNReward reward) {
        this.rewards.add(reward);
    }

    protected abstract Event cloneSpecific();

    public abstract void recordKill(Location location, Participant damager, Participant damaged);

    public abstract void recordDamage(Participant damager, Participant damaged, Location location, double damage);

    public abstract Collection<Participant> getRewardableParticipants();

    public void start() {
        this.startTime = System.currentTimeMillis();
        this.participants.clear();
        this.kills.clear();
        startEvent();
    }

    public abstract void startEvent();

    public abstract Side getWinner();

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getCommandName() {
        return commandName;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public void setSpawnLocation(Location spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public abstract boolean spawn(Player player);
}
