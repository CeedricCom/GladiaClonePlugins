package com.ceedric.event.eventmobs.controller.command;

import com.ceedric.event.eventmobs.EventMobs;
import com.ceedric.event.eventmobs.Permissions;
import com.ceedric.event.eventmobs.view.EventCommandView;
import me.deltaorion.common.command.CommandException;
import me.deltaorion.common.command.FunctionalCommand;
import me.deltaorion.common.command.sent.SentCommand;

public class ViewCommand extends FunctionalCommand {

    private final EventMobs plugin;
    private final EventCommandView view = new EventCommandView();

    protected ViewCommand(EventMobs plugin) {
        super(Permissions.VIEW_COMMAND);
        this.plugin = plugin;
    }

    @Override
    public void commandLogic(SentCommand command) throws CommandException {
        view.displayWorld(command.getSender(), plugin.getWorld());
    }
}
