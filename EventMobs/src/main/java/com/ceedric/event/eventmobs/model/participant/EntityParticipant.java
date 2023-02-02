package com.ceedric.event.eventmobs.model.participant;

import com.ceedric.event.eventmobs.StringUtil;
import com.ceedric.event.eventmobs.model.boss.BossSideEnum;
import org.bukkit.entity.EntityType;

import java.util.UUID;

public class EntityParticipant extends Participant {

    private final EntityType type;

    public EntityParticipant(EntityType type) {
        super(getId(type));
        this.type = type;
    }

    @Override
    public String getName() {
        return "#" + StringUtil.getFriendlyName(type);
    }

    @Override
    public Participant clone() {
        EntityParticipant participant = new EntityParticipant(type);
        participant.addDamage(getDamage());
        return participant;
    }

    public static UUID getId(EntityType type) {
        return new UUID(type.ordinal(),type.ordinal());
    }
}
