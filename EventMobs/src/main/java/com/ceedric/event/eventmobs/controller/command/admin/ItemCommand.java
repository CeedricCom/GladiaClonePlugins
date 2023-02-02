package com.ceedric.event.eventmobs.controller.command.admin;

import com.ceedric.event.eventmobs.EventsPlugin;
import com.ceedric.event.eventmobs.Permissions;
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
        Player player = command.getArgOrFail(0).parse(Player.class);
        if(player==null)
            return;

        String itemName = command.getArgOrFail(1).asString();
        CustomItem generator = plugin.getCustomItemManager().getItem(itemName);

        if(generator==null)
            throw new CommandException("Unknown Item '"+itemName+"'");

        ItemStack itemStack = generator.newCustomItem();
        if (player.getInventory().firstEmpty() == -1) {
            player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
        } else {
            player.getInventory().addItem(itemStack);
        }
    }
}
