package com.ceedric.event.eventmobs.model;

import com.ceedric.event.eventmobs.StringUtil;
import com.ceedric.event.eventmobs.model.participant.Participant;
import com.ceedric.event.eventmobs.model.participant.PlayerParticipant;
import com.ceedric.event.eventmobs.model.reward.Reward;
import com.ceedric.event.eventmobs.model.reward.TopNReward;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class EventService {

    private final Map<String,Event> events;

    public EventService() {
        this.events = new HashMap<>();
    }

    public void addEvent(Event event) {
        this.events.put(event.getName(),event);
    }

    public Event getEvent(String name) {
        return this.events.get(name);
    }

    public Collection<Event> getEvents() {
        return Collections.unmodifiableCollection(this.events.values());
    }

    @Nullable
    public Event getEventFromWorld(World world) {
        for(Event event : getEvents()) {
            if(event.getWorld().equals(world))
                return event;
        }

        return null;
    }

    public void clearEvents() {
        this.events.clear();
    }

    public void distributeRewards(Event event) {
        //rank participants by damage
        Collection<Participant> participants = event.getRewardableParticipants();

        int count = 0;
        for(Participant participant : participants) {
            if(participant instanceof PlayerParticipant player) {
                boolean sendTopNMessage =false;
                Player bukkitPlayer = player.getPlayer();
                for (TopNReward reward : event.getRewards()) {
                    if (count < reward.getN()) {
                        if(!sendTopNMessage) {
                            if(bukkitPlayer!=null) {
                                bukkitPlayer.sendMessage(ChatColor.GOLD + "You have received the following rewards for being the " + ChatColor.YELLOW + "top "
                                        + StringUtil.getOrdinal(count+1) +ChatColor.GOLD+" damager");
                            }
                            sendTopNMessage = true;
                        }
                        for(Reward r : reward.getRewards()) {
                            if(bukkitPlayer==null) {
                                player.addReward(r);
                            } else {
                                r.giveReward(bukkitPlayer);
                                bukkitPlayer.sendMessage(" - "+ChatColor.translateAlternateColorCodes('&',r.getName()));
                            }
                        }
                    }
                }
                count++;
            }
        }
    }

    public void recordDamage(Location location, Entity entity, Entity victim, String cause, double damage) {
        Event event = getEventFromWorld(location.getWorld());
        if(event==null)
            return;

        Participant damager = event.getOrMakeParticipant(entity,cause);
        Participant damaged = event.getOrMakeParticipant(victim,cause);

        event.recordDamage(damager,damaged,location,damage);
    }

    public void recordKill(Entity killer, String cause, Entity victim, Location location) {
        Event event = getEventFromWorld(location.getWorld());
        if(event==null)
            return;

        Participant damager = event.getOrMakeParticipant(killer,cause);
        Participant damaged = event.getOrMakeParticipant(victim,cause);

        event.recordKill(location,damager,damaged);
    }

}
