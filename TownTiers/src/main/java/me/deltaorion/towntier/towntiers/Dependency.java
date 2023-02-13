package me.deltaorion.towntier.towntiers;

import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;

public enum Dependency {

    TOWNY("Towny",true),
    SIEGEWAR("SiegeWar",false),
    MCMMO("mcMMO",false),
    COMBATPLUS("CombatPlus",false),
    COMBATLOGX("CombatLogX",false);

    private final String name;
    private final boolean required;
    private Plugin plugin;
    private boolean active = false;

    private static final HashMap<String,Dependency> byName = new HashMap<>();

    Dependency(String name, boolean required) {
        this.name = name;
        this.required = required;
    }

    public String getName() {
        return name;
    }

    public boolean isRequired() {
        return required;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public boolean isActive() {
        return active;
    }

    static {
        for(Dependency dependency : Dependency.values()) {
            byName.put(dependency.name,dependency);
        }

        check();
    }

    public static void check() {
        for(Dependency dependency : Dependency.values()) {
            boolean enabled = Bukkit.getServer().getPluginManager().isPluginEnabled(dependency.name);
            if(enabled) {
                Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin(dependency.name);
                dependency.active = true;
                dependency.plugin = plugin;
            } else {
                if(dependency.required) {
                    shutdown(dependency);
                } else {
                    TownTiers.getInstance().getLogger().warning("Dependency '"+dependency.name+"' was not found. However " +
                            "it is not essential to run the plugin!");
                }
            }
        }
    }

    public static void shutdown(Dependency dependency) {

        TownTiers.getInstance().getLogger().severe("=======================================================");
        TownTiers.getInstance().getLogger().severe("");
        TownTiers.getInstance().getLogger().severe("Required Dependency '"+dependency.name + "' was NOT found. Please install" +
                "the dependency to use the plugin! ");
        TownTiers.getInstance().getLogger().severe("");
        TownTiers.getInstance().getLogger().severe("=======================================================");

        TownTiers.getInstance().getPluginLoader().disablePlugin(TownTiers.getInstance());
    }

    public static Dependency byName(String name) {
        return byName.get(name);
    }
}
