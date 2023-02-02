package com.ceedric.event.eventmobs.model.participant;

import com.ceedric.event.eventmobs.model.boss.BossSideEnum;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.MythicBukkit;

import java.util.UUID;

public class MythicParticipant extends Participant {

    private final String name;

    public MythicParticipant(MythicMob mob) {
        super(getId(mob));
        this.name = mob.getInternalName();
    }

    @Override
    public String getName() {
        MythicMob mob = MythicBukkit.inst().getMobManager().getMythicMob(name).orElse(null);
        if(mob==null)
            return "";

        return mob.getDisplayName().get();
    }

    public MythicMob getMob() {
        return MythicBukkit.inst().getMobManager().getMythicMob(name).orElse(null);
    }

    public static UUID getId(MythicMob mob ) {
        return new UUID(mob.getInternalName().hashCode(),mob.getInternalName().hashCode());
    }

    public MythicParticipant clone() {
        MythicParticipant participant = new MythicParticipant(getMob());
        participant.addDamage(getDamage());
        return participant;
    }
}
