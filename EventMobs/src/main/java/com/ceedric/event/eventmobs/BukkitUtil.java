package com.ceedric.event.eventmobs;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

public class BukkitUtil {

    public static World getWorld(String name) {
        World world = Bukkit.getWorld(name);
        if(world!=null)
            return world;

        WorldCreator creator = new WorldCreator(name);
        return Bukkit.createWorld(creator);
    }
}
