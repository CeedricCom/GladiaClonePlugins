package com.ceedric.event.eventmobs.controller;

import com.ceedric.event.eventmobs.StringUtil;
import com.ceedric.event.eventmobs.model.BossWorld;
import com.ceedric.event.eventmobs.model.MythicBoss;
import com.ceedric.event.eventmobs.model.participant.PlayerParticipant;
import com.ceedric.event.eventmobs.model.reward.Reward;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class Listeners implements Listener {

    private final BossWorld world;
    private final WorldService service;

    public Listeners(BossWorld world, WorldService service) {
        this.world = world;
        this.service = service;
    }

    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(MythicMobDeathEvent event) {
        String name = event.getMob().getType().getInternalName();
        MythicBoss boss = world.getBoss(name);
        if(boss == null)
            return;

        service.distributeRewards(world,boss);
    }

    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        String cause = StringUtil.getFriendlyName(event.getCause().name());
        Entity damager = null;
        if(event instanceof EntityDamageByEntityEvent fight) {
            damager = fight.getDamager();
            if(damager instanceof Projectile projectile) {
                if(projectile.getShooter() instanceof Entity e)
                    damager = e;
            }
        }

        service.recordDamage(event.getEntity().getLocation(),damager,event.getEntity(),cause,event.getFinalDamage());
    }

    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onKill(EntityDeathEvent event) {
        EntityDamageEvent damageEvent = event.getEntity().getLastDamageCause();
        if(damageEvent==null)
            return;

        String cause = StringUtil.getFriendlyName(damageEvent.getCause().name());
        Entity damager = null;
        if(damageEvent instanceof EntityDamageByEntityEvent fight) {
            damager = fight.getDamager();
            if(damager instanceof Projectile projectile) {
                if(projectile.getShooter() instanceof Entity e)
                    damager = e;
            }
        }

        service.recordKill(damager,cause,event.getEntity(),event.getEntity().getLocation());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        PlayerParticipant player = world.getPlayer(event.getPlayer().getUniqueId());
        if(player==null)
            return;

        for(Reward reward : player.getOfflineRewards()) {
            reward.giveReward(event.getPlayer());
        }

        player.clearRewards();
    }
}
