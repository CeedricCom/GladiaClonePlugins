package me.nik.combatplus.utils;

import me.nik.combatplus.utils.custom.CombatPlusException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public final class ChatUtils {

    private ChatUtils() {
        throw new CombatPlusException("This is a static class dummy!");
    }

    /**
     * @param msg The message to format
     * @return The formatted message
     */
    public static String format(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    /**
     * @param message The message to send to the console
     */
    public static void consoleMessage(String message) {
        Bukkit.getServer().getConsoleSender().sendMessage(message);
    }
}