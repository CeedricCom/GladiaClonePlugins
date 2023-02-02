package com.ceedric.event.eventmobs.controller.command.admin;

import com.ceedric.event.eventmobs.EventsPlugin;
import com.ceedric.event.eventmobs.Permissions;
import com.ceedric.event.eventmobs.model.Event;
import com.ceedric.event.eventmobs.model.participant.Participant;
import com.ceedric.event.eventmobs.view.EventCommandView;
import me.deltaorion.common.command.CommandException;
import me.deltaorion.common.command.FunctionalCommand;
import me.deltaorion.common.command.sent.SentCommand;

public class ParticipantCommand extends FunctionalCommand {

    private final EventsPlugin plugin;
    private final EventCommandView view = new EventCommandView();

    protected ParticipantCommand(EventsPlugin plugin) {
        super(Permissions.PARTICIPANT_COMMAND);
        this.plugin = plugin;
    }

    @Override
    public void commandLogic(SentCommand command) throws CommandException {
        String eventName = command.getArgOrFail(0).asString();
        Event event = plugin.getService().getEvent(eventName);
        if(event==null)
            throw new CommandException("Unknown event '"+eventName+"'");

        Participant participant = event.getParticipantByName(command.getArgOrFail(1).asString());
        if(participant==null)
            throw new CommandException("Unknown participant '"+command.getArgOrFail(1).asString()+"'");

        view.displayParticipant(command.getSender(),event ,participant);
    }
}
