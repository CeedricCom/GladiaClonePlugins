package com.ceedric.event.eventmobs.controller.command;

import com.ceedric.event.eventmobs.EventMobs;
import com.ceedric.event.eventmobs.Permissions;
import com.ceedric.event.eventmobs.model.MythicBoss;
import me.deltaorion.common.command.CommandException;
import me.deltaorion.common.command.FunctionalCommand;
import me.deltaorion.common.command.sent.SentCommand;

public class DistributeCommand extends FunctionalCommand {

    private final EventMobs plugin;

    protected DistributeCommand(EventMobs plugin) {
        super(Permissions.DISTRIBUTE_COMMAND);
        this.plugin = plugin;
    }

    @Override
    public void commandLogic(SentCommand command) throws CommandException {
        String bossName = command.getArgOrFail(0).asString();
        MythicBoss boss = plugin.getWorld().getBoss(bossName);
        if(boss==null)
            return;
        command.getSender().sendMessage("Distributing rewards for '"+bossName+"'");
        plugin.getService().distributeRewards(plugin.getWorld(),boss);
    }
}
