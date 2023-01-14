package me.deltaorion.siegewarschedule;

import com.gmail.goosius.siegewar.command.SiegeWarNationAddonCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class SiegewarSchedule extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getCommand("warschedule").setExecutor(new WarScheduleCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
