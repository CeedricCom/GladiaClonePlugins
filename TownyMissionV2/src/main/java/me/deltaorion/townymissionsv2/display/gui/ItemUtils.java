package me.deltaorion.townymissionsv2.display.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Consumer;

public class ItemUtils {

    public static void transformMeta(ItemStack itemStack, Consumer<ItemMeta> metaConsumer) {
        ItemMeta meta = itemStack.getItemMeta();
        metaConsumer.accept(meta);
        itemStack.setItemMeta(meta);
    }

    public static void hideAll(ItemStack itemStack) {
        transformMeta(itemStack,meta -> {
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        });
    }

    public static ItemStack getSkull(UUID stats) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD,1);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwningPlayer(Bukkit.getOfflinePlayer(stats));
        ArrayList<String> skullMetaLore = new ArrayList<>();
        meta.setLore(skullMetaLore);
        skull.setItemMeta(meta);
        return skull;
    }
}
