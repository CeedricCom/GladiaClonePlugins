package com.ceedric.event.eventmobs.model.reward;

import me.deltaorion.bukkit.item.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class SkullReward extends ItemReward {

    private final String skullTexture;

    public SkullReward(String name, ItemStack itemStack, int amount, String skullTexture) {
        super(name, itemStack, amount);
        this.skullTexture = skullTexture;
    }

    @Override
    protected ItemStack preGiveItem(Player bukkitPlayer, ItemStack itemStack) {
        System.out.println(itemStack);
        ItemBuilder builder = new ItemBuilder(itemStack).skull(skullBuilder -> {
            skullBuilder.setTexture(skullTexture);
        });
        return builder.build();
    }
}
