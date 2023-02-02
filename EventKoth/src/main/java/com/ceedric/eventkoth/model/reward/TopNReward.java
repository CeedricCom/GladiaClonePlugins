package com.ceedric.eventkoth.model.reward;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class TopNReward {

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
}
