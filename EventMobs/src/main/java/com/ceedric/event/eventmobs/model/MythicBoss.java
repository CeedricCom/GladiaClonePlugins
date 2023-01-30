package com.ceedric.event.eventmobs.model;

import com.ceedric.event.eventmobs.model.reward.TopNReward;

import java.util.*;

public class MythicBoss {

    private final String bossName;
    private final List<TopNReward> rewards;
    private final Map<UUID,Double> damageMap;

    public MythicBoss(String bossName) {
        this.bossName = bossName;
        this.rewards = new ArrayList<>();
        this.damageMap = new HashMap<>();
    }

    public Collection<TopNReward> getRewards() {
        return Collections.unmodifiableList(rewards);
    }

    public void addDamage(UUID player, double damage) {
        Double value = damageMap.get(player);
        if(value==null) {
            this.damageMap.put(player,damage);
        } else {
            this.damageMap.put(player,damage + value);
        }
    }

    public

}
