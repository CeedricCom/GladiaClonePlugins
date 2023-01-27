package com.ceedric.tabsorter;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class TabSorter extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        registerExpansion(new TabExpansion());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void registerExpansion(PlaceholderExpansion expansion) {
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            System.out.println("Registered Placeholder");
            expansion.register();
        }
    }
}
