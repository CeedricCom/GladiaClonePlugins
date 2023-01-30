package com.ceedric.event.eventmobs.model;

import com.ceedric.event.eventmobs.model.participant.Participant;

import java.util.*;

public class BossWorld {
    private final List<MythicBoss> bosses;
    private final List<MythicKill> kills;
    private final Map<UUID, Participant> participants;

    public BossWorld() {
        bosses = new ArrayList<>();
        this.kills = new ArrayList<>();
        this.participants = new HashMap<>();
    }


}
