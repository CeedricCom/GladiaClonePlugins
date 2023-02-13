package com.ceedric.event.eventmobs.controller.command;

import com.ceedric.event.eventmobs.model.Event;
import com.ceedric.event.eventmobs.model.EventService;
import me.deltaorion.common.command.CommandException;
import me.deltaorion.common.command.sent.SentCommand;
import me.deltaorion.common.command.tabcompletion.CompletionSupplier;

import java.util.ArrayList;
import java.util.List;

public class EventCompleter implements CompletionSupplier {

    private final EventService service;
    private final boolean ignoreDisabled;

    public EventCompleter(EventService service, boolean ignoreDisabled) {
        this.service = service;
        this.ignoreDisabled = ignoreDisabled;
    }

    @Override
    public List<String> getCompletions(SentCommand command) throws CommandException {
        List<String> eventNames=  new ArrayList<>();
        for(Event event : service.getEvents()) {
            if(event.isEnabled())
                eventNames.add(event.getName());
        }

        return eventNames;
    }
}
