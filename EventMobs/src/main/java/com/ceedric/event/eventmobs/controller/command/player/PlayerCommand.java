package com.ceedric.event.eventmobs.controller.command.player;

import com.ceedric.event.eventmobs.EventsPlugin;
import com.ceedric.event.eventmobs.Permissions;
import com.ceedric.event.eventmobs.model.Event;
import me.deltaorion.common.command.Command;
import me.deltaorion.common.command.CommandException;
import me.deltaorion.common.command.FunctionalCommand;
import me.deltaorion.common.command.sent.SentCommand;

import java.util.Map;

public class PlayerCommand extends FunctionalCommand {

    private final EventsPlugin plugin;

    public PlayerCommand(EventsPlugin plugin) {
        super("");
        this.plugin = plugin;
        registerArgument("claim",new ClaimCommand(plugin));
    }

    @Override
    public void commandLogic(SentCommand command) throws CommandException {
        for(Map.Entry<String, Command>  commandFunction : getFunctions().entrySet()) {
            command.getSender().sendMessage("/"+command.getLabel()+" "+commandFunction.getKey()+": "+commandFunction.getValue().getDescription());
        }
    }


    public void deregister() {
        for(String arg : getCommandArgs()) {
            if(!arg.equals("claim")) {
                deregisterArgument(arg);
            }
        }
    }

    public void register(Event event) {
        if(event.getCommandName().equals("claim"))
            throw new IllegalArgumentException("Cannot register event with command name '"+event.getCommandName()+"'");

        registerArgument(event.getCommandName(),new SpawnCommand(event));
    }
}
