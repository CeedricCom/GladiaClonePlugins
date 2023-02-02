package com.ceedric.event.eventmobs.controller.command.admin;

import com.ceedric.event.eventmobs.Permissions;
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
    protected SkullCommand() {
        super(Permissions.SKULL_COMMAND);
    }

    @Override
    public void commandLogic(SentCommand command) throws CommandException {
        Player player = command.getArgOrFail(0).parse(Player.class);

        ItemStack itemStack = new ItemBuilder(Material.PLAYER_HEAD)
                .skull(builder -> {
                    builder.setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjlhZGZjZGM1YTc2ZTAxYmIzOWI4M2VmMjY0ZDVmNzJjNjE4MDg5ODg4ZGQ5OTU2ZmEzMzBiMzM2YjdiZWNhNSJ9fX0=");
                })
                .setDisplayName(ChatColor.GOLD+""+ChatColor.BOLD+"Head of Martia")
                .addLoreLine("")
                .addLoreLine(ChatColor.DARK_GRAY+"Proof of your bravery against")
                .addLoreLine(ChatColor.DARK_GRAY+"against the mighty Mater Martia")
                .addLoreLine("")
                .addLoreLine(ChatColor.YELLOW + "This skull was awarded to "+player.getName())
                .addLoreLine(ChatColor.YELLOW + "for being one of the three greatest")
                .addLoreLine(ChatColor.YELLOW+ "defenders against the alien invasion")
                .addLoreLine("")
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL,5)
                .addEnchantment(Enchantment.OXYGEN,5)
                .addEnchantment(Enchantment.WATER_WORKER,1)
                .build();

        if (player.getInventory().firstEmpty() == -1) {
            player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
        } else {
            player.getInventory().addItem(itemStack);
        }
    }
}
