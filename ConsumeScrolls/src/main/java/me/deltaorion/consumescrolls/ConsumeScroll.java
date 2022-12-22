package me.deltaorion.consumescrolls;

import me.deltaorion.bukkit.item.EMaterial;
import me.deltaorion.bukkit.item.ItemBuilder;
import me.deltaorion.consumescrolls.reward.ScrollReward;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class ConsumeScroll {

    private ItemBuilder item;
    private int progress;
    private int goal;
    private Material material;
    private Rarity rarity;
    private ScrollDefinition definition;

    private final String MATERIAL_KEY = "CONSUME_SCROLLS_MATERIAL";
    private final String GOAL_KEY = "CONSUME_SCROLLS_GOAL";
    private final String PROGRESS_KEY = "CONSUME_SCROLLS_PROGRESS";
    private final String DEFINITION_KEY = "CONSUME_SCROLLS_DEFINITION";

    public ConsumeScroll(ScrollDefinition definition, List<String> toolTip) {
        int goal = definition.getGoal();
        this.material = definition.getMaterial();
        this.rarity = definition.getRarity();
        this.progress = 0;
        this.definition = definition;

        ItemBuilder itemBuilder = new ItemBuilder(EMaterial.PAPER)
                .addHiddenEnchant()
                .setUnstackable()
                .setUnbreakable(true)
                .setDisplayName(rarity.getDisplayName() + " Mission")
                .addFlags(ItemFlag.HIDE_ATTRIBUTES)
                .addFlags(ItemFlag.HIDE_UNBREAKABLE)
                .addFlags(ItemFlag.HIDE_POTION_EFFECTS)
                .addFlags(ItemFlag.HIDE_PLACED_ON)
                .addFlags(ItemFlag.HIDE_ENCHANTS);

        for(String line : toolTip) {
            itemBuilder.addLoreLine(ChatColor.translateAlternateColorCodes('&',line));
        }

        itemBuilder.addLoreLine(ChatColor.GOLD + "" + ChatColor.BOLD + "Objective:");
        itemBuilder.addLoreLine(ChatColor.WHITE + "- Gather " + goal + " " + getFriendlyName() + ": 0");

        itemBuilder.addLoreLine("");
        itemBuilder.addLoreLine(ChatColor.GOLD + "" + ChatColor.BOLD + "Rewards:");
        System.out.println("This has compiled");
        for(ScrollReward reward : definition.getRewards()) {
            itemBuilder.addLoreLine(ChatColor.WHITE+"- "+reward.getName());
        }

        itemBuilder.addTag(MATERIAL_KEY,material.toString());
        itemBuilder.addTag(GOAL_KEY, String.valueOf(goal));
        itemBuilder.addTag(PROGRESS_KEY,"0");
        itemBuilder.addTag(DEFINITION_KEY,definition.getName());

        item = itemBuilder;
    }

    public ConsumeScroll(ScrollPool pool, ItemStack stack) {
        ItemBuilder item = new ItemBuilder(stack);
        progress = Integer.parseInt(item.getTagValue(PROGRESS_KEY));
        goal = Integer.parseInt(item.getTagValue(GOAL_KEY));
        material = Material.matchMaterial(item.getTagValue(MATERIAL_KEY));
        definition = pool.getScroll(item.getTagValue(DEFINITION_KEY));
        this.item = item;
    }

    public String getFriendlyName() {
        String friendlyName = material.toString().replace('_', ' ').toLowerCase();
        try {
            friendlyName = friendlyName.substring(0, 1).toUpperCase() + friendlyName.substring(1);
            return friendlyName;
        }
        catch (Exception e) {
            Bukkit.getServer().getLogger().log(Level.WARNING, "Weird Material Name - Could not capitalise");
            return material.toString();
        }
    }

    public void setProgress(int progress) {
        if(progress < 0 || progress >= goal)
            throw new IllegalArgumentException();

        this.progress = progress;
        item.addTag(PROGRESS_KEY, String.valueOf(progress));
        List<String> lore = item.build().getLore();
        String line = ChatColor.WHITE + "- Gather " + goal + " " + getFriendlyName() + ": " + progress;
        if(lore==null) {
            lore = new ArrayList<>();
            lore.add(line);
        } else {
            lore.set(lore.size() - 1, line);
        }

        item.setLore(lore);
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
        return definition!=null;
    }
}
