package me.deltaorion.townysethome;

import com.palmergames.bukkit.towny.TownyAPI;
import org.bukkit.plugin.java.JavaPlugin;

public final class TownySetHome extends JavaPlugin {

    public void onEnable() {
        final TownyAPI towny = TownyAPI.getInstance();
        if (towny == null) {
            System.out.println("Could not find Towny");
            this.getServer().getPluginManager().disablePlugin(this);
        }
        this.getServer().getPluginManager().registerEvents(new onHomeCommands(), this);
        System.out.println("Towny Set Homes Activated");
    }

    public void onDisable() {
    }
}
