package com.ceedric.event.eventmobs.model;

import com.ceedric.event.eventmobs.model.participant.BossSide;
import com.ceedric.event.eventmobs.model.participant.Participant;
import org.bukkit.Location;

public class MythicKill {

    private final Location location;
    private final Participant killer;
    private final Participant victim;
    private final BossSide side;

    public MythicKill(Location location, Participant killer, Participant victim, BossSide side) {
        this.location = location;
        this.killer = killer;
        this.victim = victim;
        this.side = side;
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

    public BossSide getSide() {
        return side;
    }
}
