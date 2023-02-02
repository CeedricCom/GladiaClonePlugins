package com.ceedric.event.eventmobs.model.participant;

public enum BossSide {
    BOSS("Aliens"),
    PLAYERS("Humans"),
    NOBODY("Nobody")
    ;

    BossSide(String formattedName) {
        this.formattedName = formattedName;
    }

    private String formattedName;

    public String getFormattedName() {
        return formattedName;
    }

    public void setFormattedName(String name) {
        this.formattedName = name;
    }
}
