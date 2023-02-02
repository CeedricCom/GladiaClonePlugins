package com.ceedric.event.eventmobs.model;

public interface Side {

    String getFormattedName();

    String getName();

    Side getOpposite();
}
