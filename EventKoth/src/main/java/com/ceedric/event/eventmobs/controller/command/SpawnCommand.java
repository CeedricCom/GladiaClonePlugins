package com.ceedric.event.eventmobs.controller.command;

import com.ceedric.event.eventmobs.EventMobs;
import com.ceedric.event.eventmobs.Permissions;
import io.lumine.mythic.bukkit.BukkitAdapter;
import me.deltaorion.common.command.CommandException;
import me.deltaorion.common.command.FunctionalCommand;
import me.deltaorion.common.command.sent.SentCommand;
import org.bukkit.Location;

public class SpawnCommand extends FunctionalCommand {

    private final EventMobs plugin;

    protected SpawnCommand( EventMobs plugin) {
        super(Permissions.SPAWN_COMMAND);
        this.plugin = plugin;
    }

    @Override
    public void commandLogic(SentCommand command) throws CommandException {
        plugin.getWorld().setEventStart();
        Location location = plugin.getIConfig().getSpawnLocation();
        plugin.getWorld().clear();
        plugin.getIConfig().getBoss().spawn(BukkitAdapter.adapt(plugin.getIConfig().getSpawnLocation()),1);
        command.getSender().sendMessage("Successfully spawned boss at ("+location.getX()+","+location.getY()+","+location.getZ()+")");
    }
}
