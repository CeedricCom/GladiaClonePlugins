package me.deltaorion.consumescrolls.reward;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemReward implements ScrollReward {

    private final ItemStack itemStack;

    public ItemReward(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public void giveReward(Player player) {
        if (player.getInventory().firstEmpty() == -1) {
            player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
        }
        else {
            player.getInventory().addItem(itemStack);
        }
    }
}
