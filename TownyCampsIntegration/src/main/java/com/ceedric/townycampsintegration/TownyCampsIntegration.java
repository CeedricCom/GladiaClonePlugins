package com.ceedric.townycampsintegration;

import com.gmail.goosius.siegewar.SiegeController;
import com.gmail.goosius.siegewar.objects.Siege;
import com.gmail.goosius.siegewar.settings.SiegeWarSettings;
import io.github.townyadvanced.townycamps.event.NewCampEvent;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public final class TownyCampsIntegration extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onCamp(NewCampEvent event) {

        int chunkX = event.getWorldCoord().getCoord().getX();
        int chunkZ = event.getWorldCoord().getCoord().getZ();

        World world = event.getPlayer().getWorld();
        Chunk chunk = world.getChunkAt(chunkX, chunkZ);

        for (Siege siege : SiegeController.getSieges()) {
            Location location = siege.getFlagLocation().clone();
            int radius = SiegeWarSettings.getWarSiegeZoneRadiusBlocks();

            if (collision(chunk.getBlock(0, 0, 0).getLocation().clone().toVector(),
                    chunk.getBlock(15, 0, 15).getLocation().clone().toVector(),
                    location.clone().toVector(), radius)) {
                event.setCancelled(true);
                event.setCancelledMessage(ChatColor.RED + "You may not place a camp in a siege zone.");
                return;
            }
        }
    }
    private boolean collision(Vector v1, Vector v2, Vector c, int radius) {

        if (v1.getX() > v2.getX())
            swap(v1, v2);

        if (v1.getZ() > v2.getZ())
            swapZ(v1, v2);

        int closestX = clamp((int) c.getX(), (int) v1.getX(), (int) v2.getX());
        int closestZ = clamp((int) c.getZ(), (int) v1.getZ(), (int) v2.getZ());

        int distanceX = (int) c.getX() - closestX;
        int distanceZ = (int) c.getZ() - closestZ;

        float distance = (float) (distanceX * distanceX) + (distanceZ * distanceZ);

        return distance <= radius * radius;
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private void swap(Vector v1, Vector v2) {
        double temp = v1.getX();
        v1.setX(v2.getX());
        v2.setX(temp);
    }

    private void swapZ(Vector v1, Vector v2) {
        double temp = v1.getZ();
        v1.setZ(v2.getZ());
        v2.setZ(temp);
    }
}
