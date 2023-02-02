package com.ceedric.event.eventmobs.controller;

import com.ceedric.event.eventmobs.model.BossWorld;
import com.ceedric.event.eventmobs.model.MythicBoss;
import com.ceedric.event.eventmobs.model.MythicKill;
import com.ceedric.event.eventmobs.model.participant.OtherParticipant;
import com.ceedric.event.eventmobs.model.participant.Participant;
import com.ceedric.event.eventmobs.model.participant.PlayerParticipant;
import com.ceedric.event.eventmobs.model.reward.Reward;
import com.ceedric.event.eventmobs.model.reward.TopNReward;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class WorldService {

    private final BossWorld world;

    public WorldService(BossWorld world) {
        this.world = world;
    }

    public void distributeRewards(BossWorld world, MythicBoss boss) {
        //rank participants by damage
        List<Participant> participants = new ArrayList<>(world.getParticipants());
        participants.sort((o1, o2) -> Double.compare(o2.getDamage(),o1.getDamage()));
        int count = 0;
        for(Participant participant : participants) {
            if(participant instanceof PlayerParticipant player) {
                boolean sendTopNMessage =false;
                Player bukkitPlayer = player.getPlayer();
                for (TopNReward reward : boss.getRewards()) {
                    if (count < reward.getN()) {
                        if(!sendTopNMessage) {
                            if(bukkitPlayer!=null) {
                                bukkitPlayer.sendMessage(ChatColor.GOLD + "You have received the following rewards for being the " + ChatColor.YELLOW + "top "+reward.getN()+ChatColor.GOLD+" damagers");
                            }
                            sendTopNMessage = true;
                        }
                        for(Reward r : reward.getRewards()) {
                            if(bukkitPlayer==null) {
                                player.addReward(r);
                            } else {
                                r.giveReward(bukkitPlayer);
                                bukkitPlayer.sendMessage(" - "+r.getName());
                            }
                        }
                    }
                }
                count++;
            }
        }
    }

    public void recordDamage(Location location, Entity entity, Entity victim, String cause, double damage) {
        if(!location.getWorld().equals(world.getWorld()))
            return;

        Participant damager = world.getOrMakeParticipant(entity,cause);
        Participant damaged = world.getOrMakeParticipant(victim,cause);
        if(damaged instanceof OtherParticipant)
            return;

        damager.addDamage(damage);
    }

    public void recordKill(Entity killer, String cause, Entity victim, Location location) {
        if(!location.getWorld().equals(world.getWorld()))
            return;

        Participant damager = world.getOrMakeParticipant(killer,cause);
        Participant damaged = world.getOrMakeParticipant(victim,cause);
        if(damaged instanceof OtherParticipant)
            return;

        MythicKill kill = new MythicKill(location,damager,damaged,damaged.getSide());
        world.addKill(kill);
    }

}
