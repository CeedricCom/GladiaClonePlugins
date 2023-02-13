package com.ceedric.event.eventmobs.config;

import com.ceedric.event.eventmobs.model.Event;

import java.util.List;

public interface EventConfig {

    List<Event> getEvents();

    void reload();

}
