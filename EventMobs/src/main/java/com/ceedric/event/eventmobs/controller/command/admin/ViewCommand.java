package com.ceedric.event.eventmobs.controller.command.admin;

import com.ceedric.event.eventmobs.EventsPlugin;
import com.ceedric.event.eventmobs.Permissions;
import com.ceedric.event.eventmobs.model.Event;
import com.ceedric.event.eventmobs.view.EventCommandView;
import me.deltaorion.common.command.CommandException;
import me.deltaorion.common.command.FunctionalCommand;
import me.deltaorion.common.command.sent.SentCommand;

public class ViewCommand extends FunctionalCommand {

    private final EventsPlugin plugin;
    private final EventCommandView view = new EventCommandView();

    protected ViewCommand(EventsPlugin plugin) {
        super(Permissions.VIEW_COMMAND);
        this.plugin = plugin;
    }

    @Override
    public void commandLogic(SentCommand command) throws CommandException {
        String eventName = command.getArgOrFail(0).asString();
        Event event = plugin.getService().getEvent(eventName);
        if(event==null)
            throw new CommandException("Unknown event '"+eventName+"'");
        view.displayEvent(command.getSender(), event);
    }
}
