package me.deltaorion.fixmaxhealth;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class FixMaxHealth extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(this,this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void setMaxHealth(Player player) {
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
        player.setHealth(20);
    }

    @EventHandler (ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        setMaxHealth(event.getPlayer());
    }

    @EventHandler (ignoreCancelled = true)
    public void onDeath(PlayerRespawnEvent event) {
        setMaxHealth(event.getPlayer());
    }
}
