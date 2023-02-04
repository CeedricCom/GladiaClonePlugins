package com.ceedric.event.eventmobs.model.reward;

import com.ceedric.event.eventmobs.model.participant.PlayerParticipant;
import org.bukkit.entity.Player;

public interface Reward {

    void giveReward(PlayerParticipant player);

    String getName();
}

