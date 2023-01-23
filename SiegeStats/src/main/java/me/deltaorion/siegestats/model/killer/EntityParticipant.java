package me.deltaorion.siegestats.model.killer;

import me.deltaorion.siegestats.StringUtil;
import org.bukkit.entity.EntityType;

import java.util.UUID;

public class EntityParticipant extends Participant {

    private final EntityType type;

    public EntityParticipant(EntityType type) {
        super(new UUID(type.ordinal(),type.ordinal()));
        this.type = type;
    }

    @Override
    public String getName() {
        return "#" + StringUtil.getFriendlyName(type);
    }

    public EntityType getType() {
        return type;
    }

    @Override
    public EntityParticipant clone() {
        EntityParticipant participant = new EntityParticipant(type);
        participant.addDamage(this.getDamage());
        return participant;
    }
}
