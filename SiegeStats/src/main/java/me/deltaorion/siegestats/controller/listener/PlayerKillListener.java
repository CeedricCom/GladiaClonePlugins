package me.deltaorion.siegestats.controller.listener;

import com.gmail.goosius.siegewar.settings.SiegeWarSettings;
import me.deltaorion.siegestats.service.SiegeService;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerKillListener implements Listener {

    private final SiegeService siegeService;

    public PlayerKillListener(SiegeService siegeService) {
        this.siegeService = siegeService;
    }

    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onKill(PlayerDeathEvent event) {
        EntityDamageEvent damage = event.getPlayer().getLastDamageCause();
        if(damage == null) {
            return;
        }

        EntityDamageEvent.DamageCause cause = damage.getCause();
        Entity damager = null;
        if(damage instanceof EntityDamageByEntityEvent entityDamage) {
            damager = entityDamage.getDamager();
            if(damager instanceof Projectile projectile) {
                if(projectile.getShooter() instanceof Entity entity) {
                    damager = entity;
                }
            }
        }
        siegeService.evaluateKill(damager,event.getPlayer(),event.getPlayer().getLocation(),cause.name());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if(!(event.getEntity() instanceof Player player))
            return;

        EntityDamageEvent.DamageCause cause = event.getCause();
        Entity damager = null;
        if(event instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent entityDamage = (EntityDamageByEntityEvent) event;
            damager = entityDamage.getDamager();
        }

        siegeService.evaluateDamage(damager,player,cause.name(),event.getFinalDamage());
    }

    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onQuit(PlayerQuitEvent event) {
        if(event.getPlayer().isDead()
                && SiegeWarSettings.getKillPlayersWhoLogoutInSiegeZones()) {

            siegeService.evaluateKill(null, event.getPlayer(), event.getPlayer().getLocation(), "logoff");
            siegeService.evaluateDamage(null,event.getPlayer(),"logoff",event.getPlayer().getLastDamage());
        }
    }

}
