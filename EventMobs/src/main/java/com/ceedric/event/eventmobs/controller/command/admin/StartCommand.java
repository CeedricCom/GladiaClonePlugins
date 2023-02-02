package com.ceedric.event.eventmobs.controller.command.admin;

import com.ceedric.event.eventmobs.EventsPlugin;
import com.ceedric.event.eventmobs.Permissions;
import com.ceedric.event.eventmobs.model.Event;
import io.lumine.mythic.bukkit.BukkitAdapter;
import me.deltaorion.common.command.CommandException;
import me.deltaorion.common.command.FunctionalCommand;
import me.deltaorion.common.command.sent.SentCommand;
import org.bukkit.Location;

public class StartCommand extends FunctionalCommand {

    private final EventsPlugin plugin;

    protected StartCommand(EventsPlugin plugin) {
        super(Permissions.START_COMMAND);
        this.plugin = plugin;
    }

    @Override
    public void commandLogic(SentCommand command) throws CommandException {
        String eventName = command.getArgOrFail(0).asString();
        Event event = plugin.getService().getEvent(eventName);
        if(event==null)
            throw new CommandException("Unknown event '"+eventName+"'");

        event.start();

        command.getSender().sendMessage("Successfully started '"+eventName+"'");
    }
}
