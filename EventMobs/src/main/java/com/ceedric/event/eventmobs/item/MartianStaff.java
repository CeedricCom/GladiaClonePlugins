package com.ceedric.event.eventmobs.item;

import com.gmail.goosius.siegewar.SiegeController;
import com.gmail.goosius.siegewar.objects.Siege;
import com.gmail.goosius.siegewar.settings.SiegeWarSettings;
import me.deltaorion.bukkit.item.EMaterial;
import me.deltaorion.bukkit.item.ItemBuilder;
import me.deltaorion.bukkit.item.custom.CustomItem;
import me.deltaorion.bukkit.item.custom.CustomItemEvent;
import me.deltaorion.bukkit.item.custom.ItemEventHandler;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.util.Vector;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class MartianStaff extends CustomItem {

    private long lastUsed = 0;
    private final Duration cooldown = Duration.of(4, ChronoUnit.SECONDS);

    public MartianStaff() {
        super("martianstaff",new ItemBuilder(EMaterial.TRIDENT)
        .addEnchantment(Enchantment.DAMAGE_ALL,5)
        .addEnchantment(Enchantment.LOYALTY,3)
        .addEnchantment(Enchantment.CHANNELING,1)
        .addEnchantment(Enchantment.DURABILITY,3)
                .addEnchantment(Enchantment.MENDING,1)
                .setDisplayName(ChatColor.GOLD+""+ChatColor.BOLD+"Staff of Martia")
                .addLoreLine("")
                .addLoreLine(ChatColor.DARK_GRAY+"The staff, once yielded by Mater Martia")
                .addLoreLine(ChatColor.DARK_GRAY+"is a weapon of extreme power")
                .addLoreLine("")
                .addLoreLine(ChatColor.YELLOW+"Right click on your enemy to launch")
                .addLoreLine(ChatColor.YELLOW+"them away with great strength!")
                .addLoreLine("")
                .addLoreLine(ChatColor.DARK_RED+"Does not work in siegezones!")
                .build());
    }

    @ItemEventHandler(playerOnly = true)
    public void onRightClick(CustomItemEvent<PlayerInteractEntityEvent> e) {
        PlayerInteractEntityEvent event = e.getEvent();

        if(!(event.getRightClicked() instanceof LivingEntity entity))
            return;

        int minSiegeDistance = SiegeWarSettings.getWarSiegeZoneRadiusBlocks()+50;

        for(Siege siege : SiegeController.getSieges()) {
            double siegeDistance = e.getEntity().getLocation().distance(siege.getFlagLocation()) + 50;
            if(siegeDistance < minSiegeDistance) {
                event.getPlayer().sendMessage(ChatColor.RED+"You may not use this item within '"+minSiegeDistance+"' of a siege");
                return;
            }
        }

        long now = System.currentTimeMillis();
        if(now-lastUsed < cooldown.toMillis())
            return;

        lastUsed = System.currentTimeMillis();

        Vector dir = event.getPlayer().getLocation().toVector().subtract(entity.getLocation().toVector());
        Vector norm = dir.normalize();
        norm = norm.setY(-0.3);

        entity.setVelocity(norm.multiply(-1.5f));
        entity.damage(45);

        Location l = event.getPlayer().getLocation();
        World world = l.getWorld();
        world.playSound(l, Sound.ENTITY_WARDEN_SONIC_BOOM,1.5f,1f);
        world.spawnParticle(Particle.ASH,l,5);
    }
}
