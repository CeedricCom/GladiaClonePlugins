package com.ceedric.event.eventmobs.controller.command.admin;

import com.ceedric.event.eventmobs.EventsPlugin;
import com.ceedric.event.eventmobs.Permissions;
import com.ceedric.event.eventmobs.controller.command.EventCompleter;
import com.ceedric.event.eventmobs.model.Event;
import me.deltaorion.common.command.CommandException;
import me.deltaorion.common.command.FunctionalCommand;
import me.deltaorion.common.command.sent.SentCommand;

public class EnableCommand extends FunctionalCommand {

    private final EventsPlugin plugin;

    protected EnableCommand(EventsPlugin plugin) {
        super(Permissions.ENABLE_COMMAND);
        this.plugin = plugin;
        registerCompleter(1,new EventCompleter(plugin.getService(), false));
    }

    @Override
    public void commandLogic(SentCommand command) throws CommandException {
        String eventName = command.getArgOrFail(0).asString();
        Event event = plugin.getService().getEvent(eventName);
        if(event==null)
            throw new CommandException("Unknown event '"+eventName+"'");

        event.setEnabled(!event.isEnabled());
        if(event.isEnabled()) {
            plugin.getPlayerCommand().register(event);
        } else {
            plugin.getPlayerCommand().register(event);
        }

        if(event.isEnabled()) {
            command.getSender().sendMessage("Event has been enabled");
        } else if(!event.isEnabled()) {
            command.getSender().sendMessage("Event has been disabled");
        }
    }
}
