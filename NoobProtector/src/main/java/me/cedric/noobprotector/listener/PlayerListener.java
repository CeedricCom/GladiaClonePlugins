package me.cedric.noobprotector.listener;

import com.palmergames.bukkit.towny.event.TownAddResidentRankEvent;
import com.palmergames.bukkit.towny.event.nation.NationRankAddEvent;
import me.cedric.noobprotector.NoobProtector;
import me.cedric.noobprotector.util.NPUtil;
import org.bukkit.ChatColor;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {

    NoobProtector plg;
    NPUtil u;

    public PlayerListener(final NoobProtector plg) {
        this.plg = plg;
        this.u = plg.u;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        if (p.hasMetadata("NP-checktime")) {
            p.removeMetadata("NP-checktime", this.plg);
        }

        if (!p.hasPlayedBefore() && this.plg.joinprotect) {
            this.plg.players.addPlayer(p);
        }

        this.plg.players.updatePlayerPVP(p);
        this.plg.players.printPlayerProtected(p, false, true);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getEntityType() != EntityType.PLAYER) {
            return;
        }

        if (event.getDamager().getType() != EntityType.PLAYER) {
            return;
        }

        final Player p1 = (Player) event.getEntity();
        final Player p2 = (Player) event.getDamager();
        if (this.plg.players.getPvpOff(p1) || this.plg.players.getPvpOff(p2)) {
            event.setCancelled(true);
            this.informFailedAttack(p2, p1);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onProjectileDamage(EntityDamageEvent event) {
        if (event.getEntityType() != EntityType.PLAYER) {
            return;
        }
        if (!(event instanceof EntityDamageByEntityEvent)) {
            return;
        }
        final EntityDamageByEntityEvent evdm = (EntityDamageByEntityEvent)event;
        if (!(evdm.getDamager() instanceof Projectile)) {
            return;
        }
        final Projectile prj = (Projectile)evdm.getDamager();
        if (prj.getShooter() == null || !(prj.getShooter() instanceof Player)) {
            return;
        }
        final Player p1 = (Player)event.getEntity();
        final Player p2 = (Player)prj.getShooter();
        if (this.plg.players.getPvpOff(p1) || this.plg.players.getPvpOff(p2)) {
            event.setCancelled(true);
            this.informFailedAttack(p2, p1);
        }
    }

    public void informFailedAttack(Player atacker, Player defender) {
        if (this.plg.players.getPvpOff(atacker)) {
            this.u.printMSG(atacker, "msg_youcantattack", 'c', '6', "/pvp-on");
        } else {
            this.u.printMSG(defender, "msg_warndefender", atacker.getName());
            this.u.printMSG(atacker, "msg_warnatacker", defender.getName());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        final Player p = event.getPlayer();
        if (p.hasMetadata("NP-checktime")) {
            p.removeMetadata("NP-checktime", this.plg);
        }
        this.plg.players.updatePlayTime(p);
        this.plg.players.savePlayerList();
    }

    @EventHandler
    public void onRank(NationRankAddEvent e) {
        if (isMilitaryRank(e.getRank())) {
            Player player = e.getResident().getPlayer();
            this.plg.players.unprotectPlayer(player);
            player.sendMessage(ChatColor.GOLD + "Your PVP protection was removed because you received a military rank!");
        }
    }

    @EventHandler
    public void onTown(TownAddResidentRankEvent e) {
        if (e.getRank().equalsIgnoreCase("guard")) {
            this.plg.players.unprotectPlayer(e.getResident().getPlayer());
            e.getResident().getPlayer()
                    .sendMessage(ChatColor.GOLD + "Your PVP protection was removed because you received a military rank!");
        }
    }

    private boolean isMilitaryRank(String rank) {
        return rank.equalsIgnoreCase("general") || rank.equalsIgnoreCase("captain") ||
                rank.equalsIgnoreCase("soldier") || rank.equalsIgnoreCase("coking");
    }

}
