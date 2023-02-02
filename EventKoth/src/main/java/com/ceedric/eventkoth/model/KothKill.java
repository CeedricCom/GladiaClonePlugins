package com.ceedric.eventkoth.model;

import com.ceedric.eventkoth.model.participant.Participant;
import org.bukkit.Location;

public class KothKill {
    private final long time;
    private final Location location;
    private final Participant killer;
    private final Participant victim;

    public KothKill(Location location, Participant killer, Participant victim) {
        this.location = location;
        this.killer = killer;
        this.victim = victim;
        this.time = System.currentTimeMillis();
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

    public String getDeathSide() {
        return getVictim().getSide();
    }

    public long getTime() {
        return time;
    }

    public KothKill clone() {
        return new KothKill(location, killer.clone(), victim.clone());
    }
}
