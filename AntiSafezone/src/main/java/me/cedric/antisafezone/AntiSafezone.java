package me.cedric.antisafezone;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.gmail.nossr50.api.PartyAPI;
import com.gmail.nossr50.party.PartyManager;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyWorld;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public final class AntiSafezone extends Expansion implements Listener {

    private final ICombatLogX plugin;

    public AntiSafezone(ICombatLogX plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {
        plugin.getPlugin().getServer().getPluginManager().registerEvents(this, plugin.getPlugin());
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void reloadConfig() {

    }


    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;

        if (!(event.getDamager() instanceof Player))
            return;


        Player target = (Player) event.getEntity();
        Player attacker = (Player) event.getDamager();

        if (PartyAPI.inSameParty(target, attacker) || PartyManager.areAllies(target, attacker))
            return;

        if (isSafeZone(target.getLocation()) && !isInCombat(target))
            return;

        event.setCancelled(false);
    }

    public boolean isInCombat(Player player) {
        // Make sure to check that CombatLogX is enabled before using it for anything.
        ICombatLogX plugin = (ICombatLogX) Bukkit.getPluginManager().getPlugin("CombatLogX");
        ICombatManager combatManager = plugin.getCombatManager();

        return combatManager.isInCombat(player);
    }

    public boolean isSafeZone(Location location) {
        TownyWorld townyWorld = getTownWorld(location);
        if (townyWorld == null || townyWorld.isForcePVP())
            return false;
        Town town = getTown(location);
        if (town == null || town.isPVP())
            return false;
        TownBlock townBlock = TownyAPI.getInstance().getTownBlock(location);
        return townBlock != null && !townBlock.getPermissions().pvp;
    }

    public TownyAPI getAPI() {
        return TownyAPI.getInstance();
    }

    public TownBlock getTownBlock(Location location) {
        TownyAPI api = getAPI();
        if (api == null)
            return null;
        return api.getTownBlock(location);
    }

    public TownyWorld getTownWorld(Location location) {
        TownBlock townBlock = getTownBlock(location);
        return (townBlock == null) ? null : townBlock.getWorld();
    }

    public Town getTown(Location location) {
        try {
            TownBlock townBlock = getTownBlock(location);
            if (townBlock == null)
                return null;
            return townBlock.getTown();
        } catch (NotRegisteredException ex) {
            return null;
        }
    }


}
