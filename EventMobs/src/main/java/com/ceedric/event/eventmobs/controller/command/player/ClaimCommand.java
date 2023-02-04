package com.ceedric.event.eventmobs.controller.command.player;

import com.ceedric.event.eventmobs.EventsPlugin;
import com.ceedric.event.eventmobs.Permissions;
import com.ceedric.event.eventmobs.model.Event;
import com.ceedric.event.eventmobs.model.participant.PlayerParticipant;
import me.deltaorion.common.command.CommandException;
import me.deltaorion.common.command.FunctionalCommand;
import me.deltaorion.common.command.sent.SentCommand;
import me.deltaorion.common.locale.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ClaimCommand extends FunctionalCommand {

    private final EventsPlugin plugin;

    public ClaimCommand(EventsPlugin plugin) {
        super(Permissions.CLAIM_COMMAND,"/event claim", Message.valueOf("Claims all of the rewards you could not get due to your inventory being full"));
        this.plugin = plugin;
    }

    @Override
    public void commandLogic(SentCommand command) throws CommandException {
        if(command.getSender().isConsole()) {
            throw new CommandException("Only players may use this command");
        }

        Player player = Bukkit.getPlayer(command.getSender().getUniqueId());
        int count = 0;
        for(Event event : plugin.getService().getEvents()) {
            PlayerParticipant participant = event.getPlayer(player.getUniqueId());
            if(participant!=null) {
                for(ItemStack claimable : participant.getClaimable()) {
                    if (player.getInventory().firstEmpty() == -1) {
                        player.getLocation().getWorld().dropItemNaturally(player.getLocation(),claimable);
                    } else {
                        player.getInventory().addItem(claimable);
                    }
                    count++;
                }
                participant.clearClaimable();
            }
        }

        command.getSender().sendMessage("Successfully claimed "+count+" items");
    }
}
