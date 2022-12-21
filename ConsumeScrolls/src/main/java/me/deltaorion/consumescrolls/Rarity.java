package me.deltaorion.consumescrolls;

import org.bukkit.ChatColor;

public enum Rarity {

    COMMON(ChatColor.GRAY, "Common"),
    RARE(ChatColor.GREEN, "Rare"),
    EPIC(ChatColor.DARK_PURPLE, "Epic"),
    LEGENDARY(ChatColor.GOLD, "Legendary");

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
        return ChatColor.BOLD + "" + color + "" + displayName;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }
}
