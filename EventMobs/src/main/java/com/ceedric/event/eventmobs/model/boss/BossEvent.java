package com.ceedric.event.eventmobs.model.boss;

import com.ceedric.event.eventmobs.model.Event;
import com.ceedric.event.eventmobs.model.EventKill;
import com.ceedric.event.eventmobs.model.Side;
import com.ceedric.event.eventmobs.model.participant.MythicParticipant;
import com.ceedric.event.eventmobs.model.participant.Participant;
import com.ceedric.event.eventmobs.model.participant.PlayerParticipant;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;

public class BossEvent extends Event {

    private final List<BossStart> startEntities;
    private final Map<BossSideEnum,String> names;

    public BossEvent(String name, World world) {
        super(name,world);
        this.startEntities = new ArrayList<>();
        this.names = new HashMap<>();
    }

    @Override
    protected void clearSpecific() {
        this.startEntities.clear();
    }

    @Override
    protected Event cloneSpecific() {
        BossEvent event =  new BossEvent(getName(),getWorld());
        for(BossStart start : startEntities) {
            event.addBossStart(start);
        }
        return event;
    }

    @Override
    public void recordKill(Location location, Participant killer, Participant victim) {
        BossSideEnum side = getSide(victim);
        if(side.equals(BossSideEnum.NOBODY))
            return;

        addKill(new EventKill(location,killer,victim,new BossSide(side, names)));
    }

    private BossSideEnum getSide(Participant victim) {
        if(victim instanceof MythicParticipant)
            return BossSideEnum.BOSS;

        if(victim instanceof PlayerParticipant)
            return BossSideEnum.PLAYERS;

        return BossSideEnum.NOBODY;
    }

    @Override
    public void recordDamage(Participant damager, Participant damaged, Location location, double damage) {
        BossSideEnum side = getSide(damager);
        if(side.equals(BossSideEnum.NOBODY))
            return;

        damager.addDamage(damage);
    }

    @Override
    public Collection<Participant> getRewardableParticipants() {
        List<Participant> participants = new ArrayList<>();
        for(Participant participant : getParticipants()) {
            if(participant instanceof PlayerParticipant) {
                participants.add(participant);
            }
        }

        participants.sort((o1, o2) -> Double.compare(o2.getDamage(),o1.getDamage()));
        return participants;
    }

    public void startEvent() {
        for(BossStart start : startEntities) {
            start.setAlive(true);
            if(start.getBossMob() == null) {
                Bukkit.getLogger().severe("Could not find boss of '"+start.getBossName()+"'");
            } else {
                start.getBossMob().spawn(BukkitAdapter.adapt(start.getSpawnLocation()),1);
            }
        }
    }

    @Override
    public Side getWinner() {
        return new BossSide(BossSideEnum.PLAYERS, names);
    }

    @Override
    public boolean spawn(Player player) {
        player.teleport(getSpawnLocation());
        return true;
    }

    public boolean recordKill(MythicMob mob) {
        if(!isEnabled())
            return false;

        boolean killedBoss = false;
        for(BossStart startEntity : startEntities) {
            if(startEntity.getBossName().equals(mob.getInternalName())) {
                if(startEntity.isAlive()) {
                    startEntity.setAlive(false);
                    killedBoss = true;
                    break;
                }
            }
        }

        if(!killedBoss)
            return false;

        for(BossStart start : startEntities) {
            if(start.isAlive())
                return false;
        }

        return true;
    }

    public List<BossStart> getStartEntities() {
        return Collections.unmodifiableList(startEntities);
    }

    public void addBossStart(BossStart start) {
        this.startEntities.add(start);
    }

    public void addName(BossSideEnum side, String name) {
        this.names.put(side,name);
    }
}
