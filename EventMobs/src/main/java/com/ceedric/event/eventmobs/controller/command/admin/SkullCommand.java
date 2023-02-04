package com.ceedric.event.eventmobs.controller.command.admin;

import com.ceedric.event.eventmobs.EventsPlugin;
import com.ceedric.event.eventmobs.Permissions;
import com.ceedric.event.eventmobs.model.Event;
import com.ceedric.event.eventmobs.model.participant.Participant;
import com.ceedric.event.eventmobs.model.participant.PlayerParticipant;
import com.ceedric.event.eventmobs.model.reward.ItemReward;
import me.deltaorion.bukkit.item.ItemBuilder;
import me.deltaorion.common.command.CommandException;
import me.deltaorion.common.command.FunctionalCommand;
import me.deltaorion.common.command.sent.SentCommand;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SkullCommand extends FunctionalCommand {

    private final EventsPlugin plugin;

    protected SkullCommand(EventsPlugin plugin) {
        super(Permissions.SKULL_COMMAND);
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

        ItemStack itemStack = new ItemBuilder(Material.PLAYER_HEAD)
                .skull(builder -> {
                    builder.setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjlhZGZjZGM1YTc2ZTAxYmIzOWI4M2VmMjY0ZDVmNzJjNjE4MDg5ODg4ZGQ5OTU2ZmEzMzBiMzM2YjdiZWNhNSJ9fX0=");
                })
                .setDisplayName(ChatColor.DARK_RED+""+ChatColor.BOLD+"Skull of Martia")
                .addLoreLine("")
                .addLoreLine(ChatColor.GRAY+"It is said that when worn,")
                .addLoreLine(ChatColor.GRAY+"the power of Mater Martia")
                .addLoreLine(ChatColor.GRAY+"is imbued within you. Be")
                .addLoreLine(ChatColor.GRAY+"weary of the emotions you")
                .addLoreLine(ChatColor.GRAY+"may feel when wearing this,")
                .addLoreLine(ChatColor.GRAY+"If you overcome with an urge to")
                .addLoreLine(ChatColor.GRAY+"take over a planet, please")
                .addLoreLine(ChatColor.GRAY+"remove immediately")
                .addLoreLine("")
                .addLoreLine(ChatColor.RED + "This skull was awarded to "+player.getName())
                .addLoreLine(ChatColor.RED + "for being one of the three greatest")
                .addLoreLine(ChatColor.RED+ "defenders during the alien invasion")
                .addLoreLine("")
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL,5)
                .addEnchantment(Enchantment.OXYGEN,5)
                .addEnchantment(Enchantment.WATER_WORKER,1)
                .build();

        ItemReward reward = new ItemReward(itemStack.getItemMeta().getDisplayName(),itemStack,1);
        reward.giveReward(player);
    }
}
