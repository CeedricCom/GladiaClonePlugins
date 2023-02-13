package com.ceedric.event.eventmobs.config;

import com.ceedric.event.eventmobs.model.Event;
import com.ceedric.event.eventmobs.model.boss.BossSideEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TestEventConfig implements EventConfig {

    private final List<Event> events;
    private final Map<BossSideEnum,String> names;

    public TestEventConfig(Map<BossSideEnum, String> names) {
        this.names = names;
        events = new ArrayList<>();
    }

    @Override
    public List<Event> getEvents() {
        return events;
    }

    @Override
    public void reload() {

    }
}
