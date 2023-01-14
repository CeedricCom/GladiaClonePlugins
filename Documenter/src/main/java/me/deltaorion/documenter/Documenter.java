package me.deltaorion.documenter;

import me.deltaorion.bukkit.plugin.plugin.BukkitPlugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class Documenter extends BukkitPlugin {


    @Override
    public void onPluginEnable() {
        registerCommand(new DocumentationCommand(this),"documenter");
    }
}
