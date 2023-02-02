package com.ceedric.event.eventmobs.config;

import com.ceedric.event.eventmobs.model.Event;
import com.ceedric.event.eventmobs.model.boss.BossSideEnum;

import java.util.List;
import java.util.Map;

public interface EventConfig {

    List<Event> getEvents();

    void reload();

    Map<BossSideEnum, String> getNames();

}
