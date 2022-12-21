package me.deltaorion.townymissionsv2.mission.reward;

import java.util.List;

public interface Rewardable {

    public void distributeRewards();

    public List<AbstractReward> getRewards();

    public void complete();

}
