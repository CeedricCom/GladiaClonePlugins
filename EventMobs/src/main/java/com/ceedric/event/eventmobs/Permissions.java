package com.ceedric.event.eventmobs;

import com.ceedric.event.eventmobs.controller.command.admin.AdminCommand;

public class Permissions {

    public final static String BASE = "eventmobs";
    public final static String COMMAND = BASE + ".command";
    public static final String PARTICIPANT_COMMAND = COMMAND + ".participant";
    public static final String VIEW_COMMAND = COMMAND+".view";
    public static final String START_COMMAND = COMMAND + ".start";
    public static final String DISTRIBUTE_COMMAND = COMMAND+".distribute";
    public static final String RELOAD_COMMAND = COMMAND + ".reload";
    public static final String ITEM_COMMAND = COMMAND+".item";
    public static final String SKULL_COMMAND = COMMAND+".skull";
    public static final String ENABLE_COMMAND = COMMAND+".enable";

    public static final String PLAYER_COMMAND = BASE + ".player";
    public static final String SPAWN_COMMAND = PLAYER_COMMAND + ".spawn";
    public static final String BYPASS = BASE + ".bypass";
    public static final String CLAIM_COMMAND = PLAYER_COMMAND + ".claim";
}
