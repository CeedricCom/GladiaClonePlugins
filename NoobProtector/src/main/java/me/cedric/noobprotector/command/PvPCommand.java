package me.cedric.noobprotector.command;

import me.cedric.noobprotector.NoobProtector;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class PvPCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        NoobProtector plugin = NoobProtector.i;

        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("noob-protector.pvp-on")) {
            return true;
        }

        if (plugin.players.getPvpOff(player)) {
            if (checkPvpOnCooldown(player)) {
                plugin.players.unprotectPlayer(player);
                plugin.u.printMSG(player, "msg_warnpvpon");
            } else {
                plugin.players.printPlayerProtected(player, true, false);
                plugin.u.printMSG(player, "msg_pvponcooldown", "/pvp-on", plugin.pvponcooldown);
            }
        } else {
            plugin.u.printMSG(player, "msg_alreadyunprotected");
        }

        return true;
    }

    private boolean checkPvpOnCooldown(final Player p) {

        NoobProtector plg = NoobProtector.i;

        final long ct = System.currentTimeMillis();
        if (!p.hasMetadata("NP-pvp-on-cooldown") ||
                ct - p.getMetadata("NP-pvp-on-cooldown").get(0).asLong() > plg.pvponcooldown * 1000L) {
            p.setMetadata("NP-pvp-on-cooldown", new FixedMetadataValue(plg, ct));
            return false;
        }
        return true;
    }






















}
