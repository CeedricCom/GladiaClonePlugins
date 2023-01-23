package me.deltaorion.siegestats.model.killer;

import java.util.UUID;

public class OtherParticipant extends Participant {

    private final String name;

    public OtherParticipant(String name) {
        super(new UUID(name.hashCode(),name.hashCode()));
        this.name = name;
    }

    @Override
    public String getName() {
        return "#" + name;
    }

    public String getRawName() {
        return name;
    }

    @Override
    public OtherParticipant clone() {
        OtherParticipant participant = new OtherParticipant(name);
        participant.addDamage(this.getDamage());
        return participant;
    }

}
