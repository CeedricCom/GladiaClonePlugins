package me.deltaorion.siegestats.model;

import com.gmail.goosius.siegewar.enums.SiegeSide;
import me.deltaorion.siegestats.model.killer.Participant;
import org.bukkit.Location;

public class SiegeKill {

    private final Participant killer;
    private final SiegeSide deathSide;
    private final Participant victim;
    private final Location location;
    private final long time;

    public SiegeKill(Participant killer, SiegeSide deathSide, Participant victim, Location location, long time) {
        this.killer = killer;
        this.victim = victim;
        this.location = location;
        this.deathSide = deathSide;
        this.time = time;
    }

    public SiegeKill(Participant killer, SiegeSide deathSide, Participant victim, Location location) {
        this(killer,deathSide,victim,location,System.currentTimeMillis());
    }

    public Participant getKiller() {
        return killer;
    }

    public SiegeSide getDeathSide() {
        return deathSide;
    }

    public Participant getVictim() {
        return victim;
    }

    public Location getLocation() {
        return location;
    }

    public long getTime() {
        return time;
    }

    @Override
    public SiegeKill clone() {
        return new SiegeKill(this.killer, this.deathSide, this.victim, this.location, this.time);
    }
}
