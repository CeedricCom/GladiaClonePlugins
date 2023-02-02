package com.ceedric.eventkoth.model.reward;

import org.bukkit.entity.Player;

public interface Reward {

    void giveReward(Player player);

    String getName();
}

