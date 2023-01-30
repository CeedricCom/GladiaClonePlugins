package com.ceedric.event.eventmobs.model.participant;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.MythicBukkit;

import java.util.UUID;

public class MythicParticipant extends Participant {

    private final String name;

    protected MythicParticipant(MythicMob mob) {
        super(new UUID(mob.getInternalName().hashCode(),mob.getInternalName().hashCode()));
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
}
