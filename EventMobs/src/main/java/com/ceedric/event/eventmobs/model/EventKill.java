package com.ceedric.event.eventmobs.model;

import com.ceedric.event.eventmobs.model.boss.BossSideEnum;
import com.ceedric.event.eventmobs.model.participant.Participant;
import org.bukkit.Location;

public class EventKill {

    private final long time;
    private final Location location;
    private final Participant killer;
    private final Participant victim;
    private final Side deathSide;

    public EventKill(Location location, Participant killer, Participant victim, Side deathSide) {
        this.location = location;
        this.killer = killer;
        this.victim = victim;
        this.deathSide = deathSide;
        this.time = System.currentTimeMillis();
    }

    public EventKill(long time ,Location location, Participant killer, Participant victim, Side deathSide) {
        this.time = time;
        this.location = location;
        this.killer = killer;
        this.victim = victim;
        this.deathSide = deathSide;
    }

    public Location getLocation() {
        return location;
    }

    public Participant getKiller() {
        return killer;
    }

    public Participant getVictim() {
        return victim;
    }

    public Side getDeathSide() {
        return deathSide;
    }

    public long getTime() {
        return time;
    }

    public EventKill clone() {
        return new EventKill(time,location,killer.clone(),victim.clone(), deathSide);
    }
}
