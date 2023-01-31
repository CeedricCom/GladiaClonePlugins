package com.ceedric.event.eventmobs.model.participant;

import java.util.UUID;

public abstract class Participant {

    private final UUID uniqueId;
    private double damage;

    protected Participant(UUID uniqueId) {
        this.uniqueId = uniqueId;
        this.damage = 0;
    }

    public void addDamage(double damage) {
        this.damage += damage;
    }

    public double getDamage() {
        return damage;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public abstract String getName();

    public abstract BossSide getSide();

    public abstract Participant clone();
}
