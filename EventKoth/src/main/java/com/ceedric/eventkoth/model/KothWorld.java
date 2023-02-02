package com.ceedric.eventkoth.model;

import com.ceedric.eventkoth.model.participant.OtherParticipant;
import com.ceedric.eventkoth.model.participant.Participant;
import com.ceedric.eventkoth.model.participant.PlayerParticipant;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class KothWorld {

    private final List<KothKill> kills;
    private final Map<UUID, Participant> participants;
    private final World world;
    private long eventStart;

    public KothWorld(World world) {
        this.world = world;
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

    public Collection<Participant> getParticipants() {
        return Collections.unmodifiableCollection(participants.values());
    }

    public void addKill(KothKill kill) {
        this.kills.add(kill);
    }

    public Collection<KothKill> getKills() {
        return Collections.unmodifiableList(kills);
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

    @NotNull
    public Participant getOrMakeParticipant(Entity entity, String cause) {
        Participant participant = null;
        if (entity instanceof Player) {
            Player player = (Player) entity;
            participant = participants.get(player.getUniqueId());
            if (participant == null) {
                participant = new PlayerParticipant(player);
                addParticipant(participant);
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


    public World getWorld() {
        return world;
    }

    public void clear() {
        kills.clear();
        participants.clear();
    }

    public void clearBosses() {
        this.kills.clear();
    }

    public KothWorld clone() {
        KothWorld world = new KothWorld(getWorld());
        world.eventStart = getEventStart();

        for(Participant participant : getParticipants()) {
            world.addParticipant(participant.clone());
        }

        for(KothKill kill : getKills()) {
            world.addKill(new KothKill(kill.getLocation(),
                    world.getParticipant(kill.getKiller().getUniqueId()),
                    world.getParticipant(kill.getVictim().getUniqueId())));
        }

        return world;
    }
}
