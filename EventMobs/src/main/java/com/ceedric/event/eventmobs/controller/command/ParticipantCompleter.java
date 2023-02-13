package com.ceedric.event.eventmobs.controller.command;

import com.ceedric.event.eventmobs.model.Event;
import com.ceedric.event.eventmobs.model.EventService;
import com.ceedric.event.eventmobs.model.participant.Participant;
import me.deltaorion.common.command.CommandException;
import me.deltaorion.common.command.sent.SentCommand;
import me.deltaorion.common.command.tabcompletion.CompletionSupplier;

import java.util.ArrayList;
import java.util.List;

public class ParticipantCompleter implements CompletionSupplier {

    private final int eventIndex;
    private final EventService service;

    public ParticipantCompleter(int eventIndex, EventService service) {
        this.eventIndex = eventIndex;
        this.service = service;
    }

    @Override
    public List<String> getCompletions(SentCommand command) throws CommandException {
        String eventName = command.getArgOrFail(eventIndex).asString();
        Event event = service.getEvent(eventName);
        if(event==null)
            return new ArrayList<>();

        List<String> names = new ArrayList<>();
        for(Participant participant : event.getParticipants()) {
            names.add(participant.getName());
        }

        return names;
    }
}
