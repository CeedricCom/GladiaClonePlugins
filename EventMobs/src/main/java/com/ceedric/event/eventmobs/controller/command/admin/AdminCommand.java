package com.ceedric.event.eventmobs.controller.command.admin;

import com.ceedric.event.eventmobs.EventsPlugin;
import com.ceedric.event.eventmobs.Permissions;
import me.deltaorion.common.command.Command;
import me.deltaorion.common.command.CommandException;
import me.deltaorion.common.command.FunctionalCommand;
import me.deltaorion.common.command.sent.SentCommand;

import java.util.Map;

public class AdminCommand extends FunctionalCommand {

    private final EventsPlugin plugin;

    public AdminCommand(EventsPlugin plugin) {
        super("");
        this.plugin = plugin;
        registerArgument("view",new ViewCommand(plugin));
        registerArgument("participant",new ParticipantCommand(plugin));
        registerArgument("start",new StartCommand(plugin));
        registerArgument("view",new ViewCommand(plugin));
        registerArgument("distribute",new DistributeCommand(plugin));
        registerArgument("report",new ReportCommand(plugin));
        registerArgument("reload",new ReloadCommand(plugin));
        registerArgument("item",new ItemCommand(plugin));
        registerArgument("skull",new SkullCommand());
    }

    @Override
    public void commandLogic(SentCommand command) throws CommandException {
        for(Map.Entry<String, Command>  commandFunction : getFunctions().entrySet()) {
            command.getSender().sendMessage("/"+command.getLabel()+" "+commandFunction.getKey()+": "+commandFunction.getValue().getDescription());
        }
    }
}
