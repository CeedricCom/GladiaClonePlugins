package me.deltaorion.consumescrolls;

import org.bukkit.ChatColor;

import java.util.Random;

public enum Rarity {

    COMMON(ChatColor.GRAY, "Common"),
    RARE(ChatColor.GREEN, "Rare"),
    EPIC(ChatColor.DARK_PURPLE, "Epic"),
    LEGENDARY(ChatColor.GOLD, "Legendary");

    private final static Random r = new Random();

    private final ChatColor color;
    private final String displayName;
    private int percentage;

    Rarity(ChatColor color, String displayName) {
        this.color = color;
        this.displayName = displayName;
    }

    public ChatColor getColor() {
        return color;
    }

    public String getDisplayName() {
        return color + "" + ChatColor.BOLD + "" + displayName;
    }

    public String getPlainDisplayName() {
        return displayName;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public static Rarity getRandom() {
        Rarity[] rarities = Rarity.values();
        int random = r.nextInt(100) + 1;
        int sum = 0;
        for(Rarity rarity : rarities) {
            sum += rarity.percentage;
            if(random <= sum)
                return rarity;
        }

        throw new IllegalStateException();
    }
}
