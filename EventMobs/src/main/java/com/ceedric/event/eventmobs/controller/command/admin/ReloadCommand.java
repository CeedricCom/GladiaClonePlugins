package com.ceedric.event.eventmobs.controller.command.admin;

import com.ceedric.event.eventmobs.EventsPlugin;
import com.ceedric.event.eventmobs.Permissions;
import me.deltaorion.common.command.CommandException;
import me.deltaorion.common.command.FunctionalCommand;
import me.deltaorion.common.command.sent.SentCommand;

public class ReloadCommand extends FunctionalCommand {

    private final EventsPlugin plugin;

    protected ReloadCommand(EventsPlugin plugin) {
        super(Permissions.RELOAD_COMMAND);
        this.plugin = plugin;
    }

    @Override
    public void commandLogic(SentCommand sentCommand) throws CommandException {
        plugin.reloadIConfig();
        sentCommand.getSender().sendMessage("Successfully reloaded config");
    }
}
