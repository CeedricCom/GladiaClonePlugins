package me.deltaorion.townymissionsv2.mission.reward.type;

import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.metadata.StringDataField;
import me.deltaorion.townymissionsv2.configuration.ConfigurationException;
import me.deltaorion.townymissionsv2.mission.reward.RewardType;
import me.deltaorion.townymissionsv2.util.TownyUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class ItemReward extends OfflinedReward {

    private final ItemStack itemStack;

    public ItemReward(ItemStack itemStack) {
        super();
        this.itemStack = itemStack;
    }

    private ItemReward(ItemStack itemStack, UUID uuid) {
        super(uuid);
        this.itemStack = itemStack;
    }

    @Override
    public String getName() {
        return itemStack.getI18NDisplayName();
    }

    @Override
    public RewardType copy() {
        return new ItemReward(itemStack);
    }

    @Override
    protected void setOnlineReward(UUID user, double amount) {
        Player player = Bukkit.getPlayer(user);
        if(player==null)
            return;

        int stackSize = itemStack.getType().getMaxStackSize();
        if(amount <= stackSize) {
            ItemStack itemStack = this.itemStack.clone();
            itemStack.setAmount((int) amount);
            giveItem(player,itemStack);
        } else {
            int stacks = (int) (amount / stackSize);
            int leftOver = (int) (amount % stackSize);

            for(int i=0;i<stacks;i++) {
                ItemStack itemStack = this.itemStack.clone();
                itemStack.setAmount(stackSize);
                giveItem(player,itemStack.clone());
            }

            if(leftOver>0) {
                ItemStack itemStack = this.itemStack.clone();
                itemStack.setAmount(leftOver);
                giveItem(player, itemStack);
            }
        }
    }

    private void giveItem(Player player, ItemStack itemStack) {
        if(player.getInventory().firstEmpty()==-1) {
            player.getWorld().dropItemNaturally(player.getLocation(),itemStack);
        } else {
            player.getInventory().addItem(itemStack);
        }
    }

    private static String toString(ItemStack itemStack) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

        dataOutput.writeObject(itemStack);
        dataOutput.flush();

        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        LinkedHashMap<String,Object> hashMap = new LinkedHashMap<>();
        hashMap.put("itemstack",itemStack.serialize());
        return hashMap;
    }

    public static ItemReward deserialize(ConfigurationSection section) {
        if(!section.contains("itemstack"))
            throw new ConfigurationException(section,"itemstack");

        try {
            ItemStack itemStack = section.getItemStack("itemstack");
            if(itemStack==null)
                throw new IllegalArgumentException();

            return new ItemReward(itemStack);

        } catch (Throwable e) {
            throw new ConfigurationException(section.getCurrentPath(),section.getConfigurationSection("itemstack").getValues(true),"Unable to load itemstack");
        }
    }

    public void loadParameters(PreparedStatement sub, UUID supUUID) throws SQLException {
        try {
            String itemStack = serializeItemStack(this.itemStack);
            sub.setString(1,supUUID.toString());
            sub.setString(2,itemStack);
            sub.addBatch();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String serializeItemStack(ItemStack itemStack) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BukkitObjectOutputStream bukkitObjectOutputStream = new BukkitObjectOutputStream(outputStream);
        bukkitObjectOutputStream.writeObject(itemStack);
        bukkitObjectOutputStream.flush();
        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }

    public static ItemReward fromSave(UUID uuid, String itemStackString) {
        ItemStack itemStack = deserializeItemStack(itemStackString);
        if(itemStack==null) {
            return null;
        } else {
            return new ItemReward(itemStack, uuid);
        }
    }

    private static ItemStack deserializeItemStack(String itemStackString) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(itemStackString));
            BukkitObjectInputStream is = new BukkitObjectInputStream(inputStream);
            return (ItemStack) is.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
