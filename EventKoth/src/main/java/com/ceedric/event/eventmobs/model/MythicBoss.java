package com.ceedric.event.eventmobs.model;

import com.ceedric.event.eventmobs.model.reward.TopNReward;

import java.util.*;

public class MythicBoss {

    private final String bossName;
    private final TreeSet<TopNReward> rewards;

    public MythicBoss(String bossName) {
        this.bossName = bossName;
        this.rewards = new TreeSet<>(Comparator.comparingInt(TopNReward::getN));
    }

    public Collection<TopNReward> getRewards() {
        return Collections.unmodifiableSet(rewards);
    }

    public void addReward(TopNReward reward) {
        this.rewards.add(reward);
    }

    public String getBossName() {
        return bossName;
    }
}
