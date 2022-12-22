package me.deltaorion.consumescrolls.reward;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemReward implements ScrollReward {

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
        for(int i=0;i<amount;i++) {
            if (player.getInventory().firstEmpty() == -1) {
                player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
            } else {
                player.getInventory().addItem(itemStack);
            }
        }
    }

    @Override
    public String getName() {
        return name;
    }
}
