package com.ceedric.event.eventmobs.model.reward;

import org.bukkit.entity.Player;

public interface Reward {

    void giveReward(Player player);

    String getName();
}

