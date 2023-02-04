package com.ceedric.event.eventmobs.controller.command.admin;

import com.ceedric.event.eventmobs.EventsPlugin;
import com.ceedric.event.eventmobs.Permissions;
import com.ceedric.event.eventmobs.model.Event;
import com.ceedric.event.eventmobs.model.participant.Participant;
import com.ceedric.event.eventmobs.model.participant.PlayerParticipant;
import com.ceedric.event.eventmobs.model.reward.ItemReward;
import me.deltaorion.bukkit.item.custom.CustomItem;
import me.deltaorion.common.command.CommandException;
import me.deltaorion.common.command.FunctionalCommand;
import me.deltaorion.common.command.sent.SentCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemCommand extends FunctionalCommand {

    private final EventsPlugin plugin;

    protected ItemCommand(EventsPlugin plugin) {
        super(Permissions.ITEM_COMMAND);
        this.plugin = plugin;
    }

    @Override
    public void commandLogic(SentCommand command) throws CommandException {
        String eventName = command.getArgOrFail(0).asString();
        Event event = plugin.getService().getEvent(eventName);
        if(event==null)
            throw new CommandException("Unknown event '"+eventName+"'");

        Participant participant = event.getParticipantByName(command.getArgOrFail(1).asString());
        if(!(participant instanceof PlayerParticipant player))
            throw new CommandException("Participant is not a player");

        String itemName = command.getArgOrFail(2).asString();
        CustomItem generator = plugin.getCustomItemManager().getItem(itemName);

        if(generator==null)
            throw new CommandException("Unknown Item '"+itemName+"'");

        ItemStack itemStack = generator.newCustomItem();
        ItemReward reward = new ItemReward(itemStack.getItemMeta().getDisplayName(),itemStack,1);
        reward.giveReward(player);
    }
}
