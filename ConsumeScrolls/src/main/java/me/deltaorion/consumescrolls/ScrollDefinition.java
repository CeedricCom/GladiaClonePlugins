package me.deltaorion.consumescrolls;

import me.deltaorion.bukkit.item.EMaterial;
import me.deltaorion.consumescrolls.reward.ScrollReward;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ScrollDefinition {

    private final String name;
    private final EMaterial material;
    private final Rarity rarity;
    private final int minGoal;
    private final int maxGoal;
    private List<ScrollReward> rewards;
    private final Random random;

    public ScrollDefinition(String name, EMaterial material, Rarity rarity, int minGoal, int maxGoal) {
        this.name = name;
        this.material = material;
        this.rarity = rarity;
        this.minGoal = minGoal;
        this.maxGoal = maxGoal;
        this.random = new Random();
        this.rewards = new ArrayList<>();
    }

    public void addReward(ScrollReward reward) {
        this.rewards.add(reward);
    }

    public EMaterial getMaterial() {
        return material;
    }

    public Rarity getRarity() {
        return rarity;
    }

    public int getMinGoal() {
        return minGoal;
    }

    public int getMaxGoal() {
        return maxGoal;
    }

    public int getGoal() {
        return randomNumber(minGoal,maxGoal);
    }

    private int randomNumber(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }

    public Collection<ScrollReward> getRewards() {
        return Collections.unmodifiableList(rewards);
    }


    public String getName() {
        return name;
    }
}
