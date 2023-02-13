package com.ceedric.event.eventmobs.controller.command.player;

import com.ceedric.event.eventmobs.Permissions;
import com.ceedric.event.eventmobs.model.Event;
import me.deltaorion.common.command.CommandException;
import me.deltaorion.common.command.FunctionalCommand;
import me.deltaorion.common.command.sent.SentCommand;
import me.deltaorion.common.locale.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SpawnCommand extends FunctionalCommand {

    private final Event event;

    protected SpawnCommand(Event event) {
        super(Permissions.SPAWN_COMMAND,"/event "+ event.getCommandName(), Message.valueOf("Teleports you to the "+event.getDisplayName()+" event"));
        this.event = event;
    }

    @Override
    public void commandLogic(SentCommand command) throws CommandException {
        if(command.getSender().isConsole())
            throw new CommandException("Only players may use this command");

        if(!event.isEnabled())
            throw new CommandException("This event is currently not running!");

        Player player = Bukkit.getPlayer(command.getSender().getUniqueId());
        if(event.spawn(player))
            command.getSender().sendMessage("Teleported you to "+event.getDisplayName());
    }
}
