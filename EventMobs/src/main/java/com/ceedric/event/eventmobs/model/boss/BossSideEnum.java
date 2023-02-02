package com.ceedric.event.eventmobs.model.boss;

public enum BossSideEnum {
    BOSS("Aliens"),
    PLAYERS("Humans"),
    NOBODY("Nobody")
    ;

    BossSideEnum(String formattedName) {
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
