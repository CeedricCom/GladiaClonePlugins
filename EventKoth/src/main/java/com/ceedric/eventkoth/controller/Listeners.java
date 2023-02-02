package com.ceedric.eventkoth.controller;

import com.ceedric.eventkoth.StringUtil;
import com.ceedric.eventkoth.model.KothWorld;
import com.ceedric.eventkoth.model.participant.PlayerParticipant;
import com.ceedric.eventkoth.model.reward.Reward;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class Listeners implements Listener {

    private final KothWorld world;
    private final WorldService service;

    public Listeners(KothWorld world, WorldService service) {
        this.world = world;
        this.service = service;
    }

    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        String cause = StringUtil.getFriendlyName(event.getCause().name());
        Entity damager = null;
        if(event instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent fight = (EntityDamageByEntityEvent) event;
            damager = fight.getDamager();
            if(damager instanceof Projectile) {
                Projectile projectile = (Projectile) damager;
                if(projectile.getShooter() instanceof Entity)
                    damager = (Entity) projectile.getShooter();
            }
        }

        service.recordDamage(event.getEntity().getLocation(), damager,event.getEntity(), cause, event.getFinalDamage());
    }

    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onKill(EntityDeathEvent event) {
        EntityDamageEvent damageEvent = event.getEntity().getLastDamageCause();
        if(damageEvent==null)
            return;

        String cause = StringUtil.getFriendlyName(damageEvent.getCause().name());
        Entity damager = null;
        if(damageEvent instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent fight = (EntityDamageByEntityEvent) damageEvent;
            damager = fight.getDamager();
            if(damager instanceof Projectile) {
                Projectile p = (Projectile) damager;
                if(p.getShooter() instanceof Entity)
                    damager = (Entity) p.getShooter();
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






















