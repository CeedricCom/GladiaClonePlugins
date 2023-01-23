package me.deltaorion.siegestats.model.killer;

import java.util.UUID;

public abstract class Participant {

    private final UUID uniqueId;
    private double damage;

    protected Participant(UUID uniqueId) {
        this.uniqueId = uniqueId;
        this.damage = damage;
    }

    public void addDamage(double damage) {
        this.damage += damage;
    }

    public double getDamage() {
        return this.damage;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public abstract String getName();

    public abstract Participant clone();
}
