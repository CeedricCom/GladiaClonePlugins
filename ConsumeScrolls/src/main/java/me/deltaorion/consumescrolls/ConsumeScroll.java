package me.deltaorion.consumescrolls;

import me.deltaorion.bukkit.item.EMaterial;
import me.deltaorion.bukkit.item.ItemBuilder;
import me.deltaorion.consumescrolls.reward.ScrollReward;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class ConsumeScroll {

    private ItemBuilder item;
    private int progress;
    private int goal;
    private Material material;
    private Rarity rarity;
    private ScrollDefinition definition;
    private UUID uniqueID;
    private int objectiveLine = 0;

    private final static String DATA_KEY = "CONSUME_SCROLLS_DATA";
    private final static Pattern SPLIT = Pattern.compile(",");

    public ConsumeScroll(ScrollDefinition definition, List<String> toolTip) {
        int goal = definition.getGoal();
        this.material = definition.getMaterial();
        this.rarity = definition.getRarity();
        this.progress = 0;
        this.definition = definition;
        this.uniqueID = UUID.randomUUID();

        ItemBuilder itemBuilder = new ItemBuilder(EMaterial.PAPER)
                .addEnchantment(Enchantment.DAMAGE_ARTHROPODS, 1)
                .setUnbreakable(true)
                .setDisplayName(rarity.getDisplayName() + " Mission")
                .addFlags(ItemFlag.HIDE_ATTRIBUTES)
                .addFlags(ItemFlag.HIDE_UNBREAKABLE)
                .addFlags(ItemFlag.HIDE_POTION_EFFECTS)
                .addFlags(ItemFlag.HIDE_PLACED_ON)
                .addFlags(ItemFlag.HIDE_ENCHANTS);


        for (String line : toolTip) {
            itemBuilder.addLoreLine(ChatColor.translateAlternateColorCodes('&', line));
        }

        itemBuilder.addLoreLine(ChatColor.GOLD + "" + ChatColor.BOLD + "Objective:");
        itemBuilder.addLoreLine(ChatColor.WHITE + "- Gather " + goal + " " + getFriendlyName() + ": 0");
        objectiveLine = toolTip.size() + 1;

        itemBuilder.addLoreLine("");
        itemBuilder.addLoreLine(ChatColor.GOLD + "" + ChatColor.BOLD + "Rewards:");
        for (ScrollReward reward : definition.getRewards()) {
            itemBuilder.addLoreLine(ChatColor.WHITE + "- " + reward.getName());
        }

        addData(itemBuilder,definition,material,progress,goal,objectiveLine,uniqueID);
        item = itemBuilder;
    }

    private void addData(ItemBuilder itemBuilder, ScrollDefinition definition, Material material, int progress, int goal, int objectiveLine, UUID uniqueID) {
        StringBuilder builder = new StringBuilder();

                builder.append(goal)
                .append(',')
                .append(progress)
                .append(',')
                .append(objectiveLine)
                .append(',')
                .append(material)
                .append(',')
                .append(uniqueID)
                .append(',')
                .append(definition.getName());

        itemBuilder.addTag(DATA_KEY,builder.toString());
    }

    public ConsumeScroll(ScrollPool pool, ItemStack stack) {
        ItemBuilder item = new ItemBuilder(stack);
        String data = item.getTagValue(DATA_KEY);
        String[] split = SPLIT.split(data);
        this.goal = Integer.parseInt(split[0]);
        this.progress = Integer.parseInt(split[1]);
        this.objectiveLine = Integer.parseInt(split[2]);
        this.material = Material.valueOf(split[3]);
        this.uniqueID = UUID.fromString(split[4]);
        this.definition = pool.getScroll(split[5]);
        this.item = item;
    }

    public String getFriendlyName() {
        String friendlyName = material.toString().replace('_', ' ').toLowerCase();
        try {
            friendlyName = friendlyName.substring(0, 1).toUpperCase() + friendlyName.substring(1);
            return friendlyName;
        } catch (Exception e) {
            Bukkit.getServer().getLogger().log(Level.WARNING, "Weird Material Name - Could not capitalise - "+material.name());
            return material.toString();
        }
    }

    public void setProgress(int progress) {
        if (progress < 0 || progress >= goal)
            throw new IllegalArgumentException();

        this.progress = progress;;
        List<String> lore = item.build().getLore();
        String line = ChatColor.WHITE + "- Gather " + goal + " " + getFriendlyName() + ": " + progress;
        if (lore == null) {
            lore = new ArrayList<>();
            lore.add(line);
        } else {
            lore.set(objectiveLine, line);
        }

        item.setLore(lore);
        addData(item,definition,material,progress,goal,objectiveLine,uniqueID);
    }


    public ItemStack getItemStack() {
        return item.build();
    }

    public int getProgress() {
        return progress;
    }

    public int getGoal() {
        return goal;
    }

    public Material getMaterial() {
        return material;
    }

    public Rarity getRarity() {
        return rarity;
    }

    public ScrollDefinition getDefinition() {
        return definition;
    }

    public boolean isValid() {
        return definition != null;
    }

}
