package com.ceedric.event.eventmobs.controller.command;

import com.ceedric.event.eventmobs.EventMobs;
import com.ceedric.event.eventmobs.Permissions;
import com.ceedric.event.eventmobs.model.BossWorld;
import com.ceedric.event.eventmobs.model.participant.Participant;
import com.ceedric.event.eventmobs.view.EventCommandView;
import me.deltaorion.common.command.CommandException;
import me.deltaorion.common.command.FunctionalCommand;
import me.deltaorion.common.command.sent.SentCommand;

public class ParticipantCommand extends FunctionalCommand {

    private final EventMobs plugin;
    private final EventCommandView view = new EventCommandView();

    protected ParticipantCommand(EventMobs plugin) {
        super(Permissions.PARTICIPANT_COMMAND);
        this.plugin = plugin;
    }

    @Override
    public void commandLogic(SentCommand command) throws CommandException {
        BossWorld world = plugin.getWorld();
        Participant participant = world.getParticipantByName(command.getArgOrFail(0).asString());
        if(participant==null)
            throw new CommandException("Unknown participant '"+command.getArgOrFail(0).asString()+"'");

        view.displayParticipant(command.getSender(),world ,participant);
    }
}
