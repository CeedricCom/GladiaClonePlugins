package com.ceedric.event.eventmobs.model.reward;

import me.deltaorion.common.locale.message.Message;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemReward implements Reward {

    private final String name;
    private final ItemStack itemStack;
    private final int amount;

    public ItemReward(String name, ItemStack itemStack, int amount) {
        this.name = name;
        this.itemStack = itemStack;
        this.amount = amount;
    }

    @Override
    public void giveReward(Player player) {
        ItemStack clone = itemStack.clone();
        List<String> lore = clone.getLore();
        List<String> newLore = new ArrayList<>();
        if(lore!=null) {
            for (String line : lore) {
                newLore.add(ChatColor.translateAlternateColorCodes('&', Message.valueOf(line).toString(player.getName())));
            }

            String json = itemStack.getItemMeta().getDisplayName();

            clone.editMeta(itemMeta -> {
                itemMeta.setLore(newLore);
                itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&',getName()));
            });
        }

        for(int i=0;i<amount;i++) {
            if (player.getInventory().firstEmpty() == -1) {
                player.getWorld().dropItemNaturally(player.getLocation(), clone);
            } else {
                player.getInventory().addItem(clone);
            }
        }
    }

    @Override
    public String getName() {
        return name;
    }
}
