package com.ceedric.event.eventmobs.model.reward;

import me.deltaorion.bukkit.item.custom.CustomItem;
import me.deltaorion.bukkit.item.custom.CustomItemManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CustomItemReward extends ItemReward {

    private final String customItemName;
    private final CustomItemManager ciManager;

    public CustomItemReward(String name, ItemStack itemStack, int amount, String customItemName, CustomItemManager ciManager) {
        super(name, itemStack, amount);
        this.customItemName = customItemName;
        this.ciManager = ciManager;
    }

    @Override
    protected ItemStack preGiveItem(Player bukkitPlayer, ItemStack itemStack) {
        CustomItem item = ciManager.getItem(customItemName);
        if(item==null)
            return itemStack;

        item.makeCustom(itemStack);
        return itemStack;
    }
}
