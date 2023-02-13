package me.deltaorion.ilegalworld;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.jetbrains.annotations.NotNull;

public class Listeners implements Listener, CommandExecutor {

    private final IlegalWorld world;
    private final World defaultWorld;

    public Listeners(IlegalWorld world, World defaultWorld) {
        this.world = world;
        this.defaultWorld = defaultWorld;
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        if (isIllegalWorld(event.getPlayer(),event.getRespawnLocation())) {
            event.setRespawnLocation(modifyLocation(event.getRespawnLocation()));
            sendMessage(event.getPlayer());
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if(isIllegalWorld(event.getPlayer(),event.getPlayer().getLocation())) {
            event.getPlayer().teleport(modifyLocation(event.getPlayer().getLocation()), PlayerTeleportEvent.TeleportCause.PLUGIN);
            sendMessage(event.getPlayer());
        }
    }

    @EventHandler
    public void onWorld(PlayerTeleportEvent event) {
        if(isIllegalWorld(event.getPlayer(),event.getTo())) {
            event.setCancelled(true);
            sendMessage(event.getPlayer());
        }
    }

    @EventHandler
    public void onWorld(PlayerPortalEvent event) {
        if(isIllegalWorld(event.getPlayer(),event.getTo())) {
            event.setCancelled(true);
            sendMessage(event.getPlayer());
        }
    }

    private boolean isIllegalWorld(Player player ,Location respawnLocation) {
        if(player.hasPermission("IllegalWorld.bypass"))
            return false;

        for(String world : world.getIllegalWorlds()) {
            if(world.equals(respawnLocation.getWorld().getName()))
                return true;
        }
        return false;
    }

    private Location modifyLocation(Location location) {
        Location l = location.clone();
        l.setWorld(defaultWorld);
        l.setY(defaultWorld.getHighestBlockYAt((int)l.getX(),(int)l.getZ()));
        return l;
    }

    private void sendMessage(Player player) {
        player.sendMessage(ChatColor.RED+"You may not be in this world. You have been moved to the overworld instead");
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length==1) {
            if(args[0].equals("reload")) {
                world.reloadConfiguration();
                sender.sendMessage("Successfully reloaded config");
                return true;
            }
        }

        if(sender.hasPermission("IllegalWorld.Command")) {
            for(Player player : Bukkit.getOnlinePlayers()) {
                if(isIllegalWorld(player,player.getLocation())) {
                    sendMessage(player);
                    player.teleport(modifyLocation(player.getLocation()));
                }
            }
        }
        sender.sendMessage("Teleported all players out of illegal worlds");
        return true;
    }
}
