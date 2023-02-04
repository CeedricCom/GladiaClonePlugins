package com.ceedric.event.eventmobs.model.participant;

import com.ceedric.event.eventmobs.model.boss.BossSideEnum;
import com.ceedric.event.eventmobs.model.reward.Reward;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class PlayerParticipant extends Participant {

    private final List<Reward> offlineRewards;
    private final List<ItemStack> claimableRewards;

    public PlayerParticipant(UUID uuid) {
        super(uuid);
        this.offlineRewards = new ArrayList<>();
        this.claimableRewards = new ArrayList<>();
    }

    public PlayerParticipant(Player player) {
        super(player.getUniqueId());
        this.offlineRewards = new ArrayList<>();
        this.claimableRewards = new ArrayList<>();
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(getUniqueId());
    }

    @Override
    public String getName() {
        OfflinePlayer player = Bukkit.getOfflinePlayer(getUniqueId());
        if(player.getName()==null)
            return "";

        return player.getName();
    }

    public Collection<Reward> getOfflineRewards() {
        return offlineRewards;
    }

    public void addReward(Reward reward) {
        this.offlineRewards.add(reward);
    }

    public void clearRewards() {
        this.offlineRewards.clear();
    }

    public PlayerParticipant clone() {
        PlayerParticipant participant = new PlayerParticipant(getUniqueId());
        participant.addDamage(getDamage());
        return participant;
    }

    public void addClaimable(ItemStack reward) {
        this.claimableRewards.add(reward);
    }

    public Collection<ItemStack> getClaimable() {
        return Collections.unmodifiableList(claimableRewards);
    }

    public void clearClaimable() {
        claimableRewards.clear();
    }

}
