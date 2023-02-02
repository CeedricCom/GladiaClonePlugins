package com.ceedric.event.eventmobs.model.participant;

import com.ceedric.event.eventmobs.model.boss.BossSideEnum;
import com.ceedric.event.eventmobs.model.reward.Reward;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class PlayerParticipant extends Participant {

    private final List<Reward> offlineRewards;

    public PlayerParticipant(UUID uuid) {
        super(uuid);
        this.offlineRewards = new ArrayList<>();
    }

    public PlayerParticipant(Player player) {
        super(player.getUniqueId());
        this.offlineRewards = new ArrayList<>();
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

}
