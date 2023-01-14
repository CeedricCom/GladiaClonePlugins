package me.deltaorion.townysethome;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import net.ess3.api.events.UserTeleportHomeEvent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class onHomeCommands implements Listener {


    private final static String BYPASS_PERM = "TownySetHome.Bypass";

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onHomeCommand(final PlayerCommandPreprocessEvent e) {
        final Player player = e.getPlayer();
        if(player.hasPermission(BYPASS_PERM))
            return;

        if (e.getMessage().contains("/sethome")) {
            try {
                final Resident resident = TownyUniverse.getInstance().getResident(e.getPlayer().getName());
                if(resident==null) {
                    player.sendMessage(ChatColor.RED + "You are not a registered resident");
                    return;
                }
                final Town residentTown = resident.getTown();
                if (TownyAPI.getInstance().isWilderness(player.getLocation())) {
                    player.sendMessage(ChatColor.RED + "You must be in your own town to use this command");
                    e.setCancelled(true);
                }
                else {
                    final Town town = TownyAPI.getInstance().getTownBlock(player.getLocation()).getTown();
                    if (!residentTown.equals(town)) {
                        player.sendMessage(ChatColor.RED + "You must be in your own town to run this command");
                        e.setCancelled(true);
                    }
                }
            }
            catch (NotRegisteredException i) {
                player.sendMessage(ChatColor.RED + "You must be in a town to use this command");
                player.sendMessage(ChatColor.GOLD + "[Tip] " + ChatColor.YELLOW + "Make a town with " + ChatColor.GOLD + "/t new [name]" + ChatColor.YELLOW + " or join one by asking around for an invite");
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onHome(final UserTeleportHomeEvent e) {
        final Location homeLocation = e.getHomeLocation();
        Player player = e.getUser().getBase();
        if(player==null)
            return;

        if(player.hasPermission(BYPASS_PERM))
            return;

        try {
            final Resident resident = TownyUniverse.getInstance().getResident(e.getUser().getName());
            if(resident == null) {
                player.sendMessage(ChatColor.RED+"You are not a registered resident!");
                return;
            }

            if (!resident.hasTown()) {
                player.sendMessage(ChatColor.RED + "You need to have a town and set your home inside your town's claims in order to use this.");
                e.setCancelled(true);
                return;
            }

            final Town town = resident.getTown();
            if (TownyAPI.getInstance().isWilderness(homeLocation)) {
                player.sendMessage(ChatColor.RED + "Your /home is set to a location in wilderness and you cannot teleport");
                e.setCancelled(true);
                return;
            }

            final TownBlock townBlock = TownyAPI.getInstance().getTownBlock(homeLocation);
            if (townBlock != null && !townBlock.getTown().equals(town)) {
                player.sendMessage(ChatColor.RED + "You cannot teleport to a home that is not in your own town!");
                e.setCancelled(true);
                return;
            }
        }
        catch (NotRegisteredException ex) {}
    }
}
