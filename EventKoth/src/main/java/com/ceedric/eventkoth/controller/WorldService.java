package com.ceedric.eventkoth.controller;

import com.ceedric.eventkoth.model.KothKill;
import com.ceedric.eventkoth.model.KothWorld;
import com.ceedric.eventkoth.model.participant.OtherParticipant;
import com.ceedric.eventkoth.model.participant.Participant;
import com.ceedric.eventkoth.model.participant.PlayerParticipant;
import com.ceedric.eventkoth.model.reward.Reward;
import com.ceedric.eventkoth.model.reward.TopNReward;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class WorldService {

    private final KothWorld world;

    public WorldService(KothWorld world) {
        this.world = world;
    }

    public void distributeRewards(KothWorld world, List<TopNReward> rewards) {
        //rank participants by damage
        List<Participant> participants = new ArrayList<>(world.getParticipants());
        participants.sort((o1, o2) -> Double.compare(o2.getDamage(),o1.getDamage()));
        int count = 0;
        for(Participant participant : participants) {
            if(participant instanceof PlayerParticipant) {
                PlayerParticipant player = (PlayerParticipant) participant;
                boolean sendTopNMessage =false;
                Player bukkitPlayer = player.getPlayer();
                for (TopNReward reward : rewards) {
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

        Participant damager = world.getOrMakeParticipant(entity, cause);
        Participant damaged = world.getOrMakeParticipant(victim, cause);
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

        KothKill kill = new KothKill(location,damager,damaged);
        world.addKill(kill);
    }
}
