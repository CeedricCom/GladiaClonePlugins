package me.deltaorion.townymissionsv2.display.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.google.common.collect.ImmutableList;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static me.deltaorion.townymissionsv2.display.gui.ItemUtils.transformMeta;

public class UtilityButtons {

    public static GuiItem getCloseButton() {
        ItemStack itemStack = new ItemStack(Material.BARRIER);
        transformMeta(itemStack, itemMeta -> {
            itemMeta.setDisplayName(ChatColor.GOLD+""+ChatColor.YELLOW+"Close");
            itemMeta.setLore(ImmutableList.of("",ChatColor.WHITE + "Left-Click to Close"));
        });

        GuiItem closeButton = new GuiItem(itemStack);
        closeButton.setAction(event -> {
            event.getWhoClicked().closeInventory();
        });

        return closeButton;
    }

    public static GuiItem getNextButton(PaginatedPane page, ChestGui gui) {
        ItemStack itemStack = new ItemStack(Material.ARROW);
        transformMeta(itemStack,meta -> {
            meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Next");
        });
        List<String> lore = getPageLore(page,"next");
        itemStack.setLore(lore);

        GuiItem next = new GuiItem(itemStack);
        next.setAction(click -> {
            if(page.getPages()==0)
                return;

            page.setPage(Math.min(page.getPage()+1,page.getPages()-1));
            itemStack.setLore(getPageLore(page,"next"));
            gui.update();
        });

        return next;
    }

    public static GuiItem getBackButton(PaginatedPane page, ChestGui gui) {
        ItemStack itemStack = new ItemStack(Material.GRAY_DYE);
        transformMeta(itemStack,meta -> {
            meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Prev");
        });
        List<String> lore = getPageLore(page,"prev");
        itemStack.setLore(lore);

        GuiItem back = new GuiItem(itemStack);
        back.setAction(click -> {

            if(page.getPages()==0)
                return;

            page.setPage(Math.max(0,page.getPage()-1));
            itemStack.setLore(getPageLore(page,"prev"));
            gui.update();
        });

        return back;
    }

    private static List<String> getPageLore(PaginatedPane pane, String action) {
        return ImmutableList.of("",ChatColor.WHITE + "Page "+(pane.getPage()+1)+"/"+Math.max(1,pane.getPages()),"",ChatColor.WHITE+"Left-Click for "+ action + " page");
    }
}
