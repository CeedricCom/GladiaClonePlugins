package me.deltaorion.townymissionsv2.plugin;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Dependency {

    //the name needed to fetch the plugin
    private final String name;
    //wheher the plugin is required or not
    private final boolean required;
    private boolean active = false;
    private final Plugin master;
    //the plugin which the master depends on
    private Plugin plugin;

    public Dependency(Plugin master, String name, boolean required) {
        this.name = name;
        this.required = required;
        this.master = master;
    }
    
    public Plugin wrap() {
        return master.getServer().getPluginManager().getPlugin(name);
    }

    public void check() {
        Plugin wrapped = wrap();

        if(wrapped==null) {
            handleMissingDepend();
            return;
        }

        if(wrapped.isEnabled()) {
            this.active = true;
            this.plugin = wrapped;
        } else {
            handleMissingDepend();
        }
    }

    public void handleMissingDepend() {
        if(required) {
            shutdown();
        } else {
            warn();
        }
    }

    private void warn() {
        master.getLogger().warning("Dependency '"+name+"' was not found. However " +
                "it is not essential to run the plugin!");
    }



    public void shutdown() {

        master.getLogger().severe("=======================================================");
        master.getLogger().severe("");
        master.getLogger().severe("Required Dependency '"+name + "' was NOT found. Please install " +
                "the dependency to use the plugin! ");
        master.getLogger().severe("");
        master.getLogger().severe("=======================================================");

        master.getServer().getPluginManager().disablePlugin(master);
    }

    public String getName() {
        return name;
    }

    public boolean isRequired() {
        return required;
    }

    public boolean isActive() {
        return active;
    }

    public Plugin getPlugin() {
        return plugin;
    }
}
