package com.ceedric.event.eventmobs.controller;

import com.benzimmer123.koth.api.events.KothWinEvent;
import com.ceedric.event.eventmobs.StringUtil;
import com.ceedric.event.eventmobs.model.Event;
import com.ceedric.event.eventmobs.model.EventService;
import com.ceedric.event.eventmobs.model.boss.BossEvent;
import com.ceedric.event.eventmobs.model.koth.KothEvent;
import com.ceedric.event.eventmobs.model.participant.PlayerParticipant;
import com.ceedric.event.eventmobs.model.reward.Reward;
import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.events.party.McMMOPartyAllianceChangeEvent;
import com.gmail.nossr50.events.party.McMMOPartyChangeEvent;
import com.gmail.nossr50.party.PartyManager;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.List;

public class Listeners implements Listener {

    private final EventService service;

    public Listeners(EventService service) {
        this.service = service;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(MythicMobDeathEvent event) {
        resolveDeath(event.getMob().getType(), BukkitAdapter.adapt(event.getMob().getLocation()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onVictory(KothWinEvent event) {
        for(Event e : service.getEvents()) {
            if(e.getWorld().equals(event.getWorld())) {
                if(e instanceof KothEvent kothEvent) {
                    kothEvent.recordWinner(event.getCapper());
                    Bukkit.broadcastMessage(ChatColor.YELLOW+"The KOTH has ended with team "+ChatColor.GOLD+kothEvent.getWinner().getFormattedName()+ChatColor.YELLOW+" coming out victorious by '"+event.getCapper()+"' capturing the point");
                    service.distributeRewards(kothEvent);
                    break;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onAlly(McMMOPartyAllianceChangeEvent event) {
        switch (event.getReason()) {
            case CUSTOM:
            case FORMED_ALLIANCE:
                Party partyA = PartyManager.getParty(event.getOldAlly());
                Party partyB = PartyManager.getParty(event.getNewAlly());
                if(partyA==null || partyB == null)
                    return;

                List<Player> members = new ArrayList<>();
                members.addAll(partyA.getOnlineMembers());
                members.addAll(partyB.getOnlineMembers());

                for (Event e : service.getEvents()) {
                    if (e instanceof KothEvent kothEvent) {
                            World kothWorld = kothEvent.getWorld();
                            for (Player player : members) {
                                if (player.getWorld().equals(kothWorld)) {
                                    event.getPlayer().sendMessage(ChatColor.RED+"You may not join this party as members are participating in the KOTH, and it already has '"+kothEvent.getPlayerCap()+"' members");
                                    event.setCancelled(true);
                                    break;
                                }
                            }
                    }
                }
                break;
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onLeave(McMMOPartyChangeEvent event) {
        switch (event.getReason()) {
            case LEFT_PARTY:
            case KICKED_FROM_PARTY:
            case DISBANDED_PARTY:
                for(Event e : service.getEvents()) {
                    if(e instanceof KothEvent) {
                        if(e.getWorld().equals(event.getPlayer().getLocation().getWorld())) {
                            event.setCancelled(true);
                            event.getPlayer().sendMessage(ChatColor.RED+"You may not leave your party while in the KOTH");
                        }
                        break;
                    }
                }
                break;
            case JOINED_PARTY:
            case CREATED_PARTY:
            case CHANGED_PARTIES:
            case CUSTOM:
            default:
                Party newParty = PartyManager.getParty(event.getNewParty());
                if(newParty!=null) {
                    for (Event e : service.getEvents()) {
                        if (e instanceof KothEvent kothEvent) {
                            if(newParty.getMembers().size()>= kothEvent.getPlayerCap()) {
                                World kothWorld = kothEvent.getWorld();
                                for (Player player : newParty.getOnlineMembers()) {
                                    if (player.getWorld().equals(kothWorld)) {
                                        event.getPlayer().sendMessage(ChatColor.RED+"You may not join this party as members are participating in the KOTH, and it already has '"+kothEvent.getPlayerCap()+"' members");
                                        event.setCancelled(true);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                break;
        }
    }

    private void resolveDeath(MythicMob mob, Location location) {
        for (Event e : service.getEvents()) {
            if (e.getWorld().equals(location.getWorld())) {
                if (e instanceof BossEvent bossEvent) {
                    if (bossEvent.recordKill(mob)) {
                        service.distributeRewards(bossEvent);
                        Bukkit.broadcastMessage(ChatColor.YELLOW+"The alien invasion is concluded with the "+ChatColor.GOLD+"humans "+ChatColor.YELLOW+"coming out victorious and defeating the aliens");
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        String cause = StringUtil.getFriendlyName(event.getCause().name());
        Entity damager = null;
        if (event instanceof EntityDamageByEntityEvent fight) {
            damager = fight.getDamager();
            if (damager instanceof Projectile projectile) {
                if (projectile.getShooter() instanceof Entity e)
                    damager = e;
            }
        }


        service.recordDamage(event.getEntity().getLocation(), damager, event.getEntity(), cause, event.getFinalDamage());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onKill(EntityDeathEvent event) {
        EntityDamageEvent damageEvent = event.getEntity().getLastDamageCause();
        if (damageEvent == null)
            return;

        String cause = StringUtil.getFriendlyName(damageEvent.getCause().name());
        Entity damager = null;
        if (damageEvent instanceof EntityDamageByEntityEvent fight) {
            damager = fight.getDamager();
            if (damager instanceof Projectile projectile) {
                if (projectile.getShooter() instanceof Entity e)
                    damager = e;
            }
        }

        service.recordKill(damager, cause, event.getEntity(), event.getEntity().getLocation());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        for (Event e : service.getEvents()) {
            PlayerParticipant player = e.getPlayer(event.getPlayer().getUniqueId());
            if (player == null)
                return;

            for (Reward reward : player.getOfflineRewards()) {
                reward.giveReward(player);
            }

            player.clearRewards();
        }
    }

}
