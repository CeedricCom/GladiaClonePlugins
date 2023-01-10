package me.deltaorion.consumescrolls;

import me.deltaorion.bukkit.item.custom.CustomItem;
import me.deltaorion.bukkit.item.custom.CustomItemEvent;
import me.deltaorion.bukkit.item.custom.CustomItemEventListener;
import me.deltaorion.bukkit.item.custom.ItemEventHandler;
import me.deltaorion.bukkit.item.position.GenericInventoryItem;
import me.deltaorion.bukkit.item.position.InventoryItem;
import me.deltaorion.bukkit.item.predicate.EventCondition;
import me.deltaorion.bukkit.item.predicate.EventPredicates;
import me.deltaorion.bukkit.item.wrapper.CIEventWrapper;
import me.deltaorion.consumescrolls.config.ScrollConfig;
import me.deltaorion.consumescrolls.reward.ScrollReward;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ConsumeScrollGenerator extends CustomItem {


    private final ConsumeScrollPlugin plugin;
    private final ScrollConfig config;
    private final ScrollPool pool;

    public ConsumeScrollGenerator(ConsumeScrollPlugin plugin, ScrollConfig config, ScrollPool pool) {
        super("SCROLL_GEN");
        this.plugin = plugin;
        this.config = config;
        this.pool = pool;
    }

    public ItemStack generate(ScrollDefinition definition) {
        ConsumeScroll scroll = new ConsumeScroll(definition,config.getToolTip());
        makeCustom(scroll.getItemStack());
        return scroll.getItemStack();
    }

    public void give(Player player, ScrollDefinition definition) {
        ItemStack mission = generate(definition);
        if (player.getInventory().firstEmpty() == -1) {
            player.getWorld().dropItemNaturally(player.getLocation(), mission);
        }
        else {
            player.getInventory().addItem(mission);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getView().getTopInventory();

        if(!inventory.getType().equals(InventoryType.SHULKER_BOX))
            return;

        if(event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)
        && event.getClick().isShiftClick()) {
            System.out.println("Handling Shift Click");
            handleShiftClick(event);
            return;
        }

        System.out.println("Handling Place Click");
        handlePlaceClick(event);
    }

    private void handlePlaceClick(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();
        if(inventory==null)
            return;

        if(!inventory.getType().equals(InventoryType.SHULKER_BOX))
            return;

        if(!isCustomItem(event.getCursor()) && !isCustomItem(event.getCurrentItem()))
            return;

        cancel(event);
    }

    private void cancel(InventoryClickEvent event) {
        event.getWhoClicked().sendMessage("Sorry! You cannot place missions in shulker boxes (stop-exploit)");
        event.setCancelled(true);
    }

    private void handleShiftClick(InventoryClickEvent event) {
        if(event.getCurrentItem()==null)
            return;

        if(!isCustomItem(event.getCurrentItem()) && !isCustomItem(event.getCursor()))
            return;

        cancel(event);
    }

    @ItemEventHandler(condition = EventCondition.ANY_HAND, playerOnly = true)
    public void onRightClick(CustomItemEvent<PlayerInteractEvent> event) {
        if(!(event.getEntity() instanceof Player player))
            return;

        InventoryItem inventoryItem = event.getItemStacks().get(0);
        ItemStack stack = inventoryItem.getItemStack();
        if(stack==null)
            return;


        ConsumeScroll scroll = new ConsumeScroll(pool, stack);
        if(!scroll.isValid())
            return;


        Material material = scroll.getMaterial();
        int diff = scroll.getGoal() - scroll.getProgress();

        for(int i=0;diff > 0 && i<player.getInventory().getSize();i++) {
            ItemStack inventoryStack = player.getInventory().getItem(i);
            if(inventoryStack!=null && inventoryStack.getType().equals(material)) {
                int amount = inventoryStack.getAmount();
                if(diff >= amount) {
                    player.getInventory().setItem(i,new ItemStack(Material.AIR));
                    diff -= amount;
                } else {
                    //diff < amount -> subtract
                    inventoryStack.setAmount(amount - diff);
                    diff = 0;
                }

            }
        }

        if(diff==0) {
            completeMission(player, inventoryItem, scroll);
        } else {
            int progress = scroll.getGoal() - diff;
            scroll.setProgress(progress);
        }
    }


    private void completeMission(Player player, InventoryItem item, ConsumeScroll scroll) {
        item.removeItem();
        displayChatReward(player);
        displayTitle(player,scroll);
        for(ScrollReward reward : scroll.getDefinition().getRewards()) {
            reward.giveReward(player);
        }
    }

    private void displayTitle(Player player, ConsumeScroll scroll) {
        if (config.showTitle()) {
            player.sendTitle(ChatColor.GOLD + "" + ChatColor.BOLD + "Mission Complete", ChatColor.WHITE + "Gather " + scroll.getGoal() + " " + scroll.getFriendlyName(), 10, 40, 10);
        }
    }

    private void displayChatReward(Player player) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',config.getChatRewardMessage()));
    }

}
