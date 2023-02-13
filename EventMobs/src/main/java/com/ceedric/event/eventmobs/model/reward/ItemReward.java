package com.ceedric.event.eventmobs.model.reward;

import com.ceedric.event.eventmobs.model.participant.PlayerParticipant;
import me.deltaorion.common.locale.message.Message;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.entity.Item;
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
    public void giveReward(PlayerParticipant player) {
        Player bukkitPlayer = player.getPlayer();
        ItemStack clone = itemStack.clone();
        List<String> lore = clone.getLore();
        List<String> newLore = new ArrayList<>();
        if(lore!=null) {
            for (String line : lore) {
                newLore.add(ChatColor.translateAlternateColorCodes('&', Message.valueOf(line).toString(player.getName())));
            }

            clone.editMeta(itemMeta -> {
                itemMeta.setLore(newLore);
                itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&',getName()));
            });
        }

        clone = preGiveItem(bukkitPlayer,clone);

        for(int i=0;i<amount;i++) {
            if (bukkitPlayer.getInventory().firstEmpty() == -1) {
                player.addClaimable(clone);
            } else {
                bukkitPlayer.getInventory().addItem(clone);
            }
        }

        postGiveItem(bukkitPlayer,clone);
    }

    protected void postGiveItem(Player bukkitPlayer, ItemStack itemStack) {

    }

    protected ItemStack preGiveItem(Player bukkitPlayer, ItemStack itemStack) {
        return itemStack;
    }

    @Override
    public String getName() {
        return name;
    }
}
