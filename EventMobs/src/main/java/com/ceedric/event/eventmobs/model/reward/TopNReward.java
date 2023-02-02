package com.ceedric.event.eventmobs.model.reward;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class TopNReward implements Comparable<TopNReward> {

    private final int n;
    private final List<Reward> rewards;

    public TopNReward(int n) {
        this.n = n;
        this.rewards = new ArrayList<>();
    }

    public void addReward(Reward reward) {
        this.rewards.add(reward);
    }

    public Collection<Reward> getRewards() {
        return Collections.unmodifiableList(rewards);
    }

    public void clearRewards() {
        this.rewards.clear();
    }

    public int getN() {
        return n;
    }

    @Override
    public int compareTo(@NotNull TopNReward o) {
        return Integer.compare(o.getN(),this.getN());
    }
}
