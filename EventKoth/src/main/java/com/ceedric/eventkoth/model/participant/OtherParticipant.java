package com.ceedric.eventkoth.model.participant;

import java.util.UUID;

public class OtherParticipant extends Participant {

    private final String name;

    public OtherParticipant(String name) {
        super(getId(name));
        this.name = name;
    }

    @Override
    public String getName() {
        return "#" + name;
    }

    @Override
    public String getSide() {
        return null;
    }

    public static UUID getId(String name) {
        return new UUID(name.hashCode(),name.hashCode());
    }

    public OtherParticipant clone() {
        OtherParticipant participant = new OtherParticipant(name);
        participant.addDamage(getDamage());
        return participant;
    }
}
