package me.deltaorion.stresstest;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

public class Teleporter {

    private final Random random = new Random();
    private final static int MIN_XZ = -15000;
    private final static int MAX_XZ = 15000;

    private int delay = 0;

    private BukkitRunnable runnable;
    private Iterator<? extends Player> players;

    public void RTP(Player player) {
        int x = random(-18432,18431);
        int z = random(-9216,9215);
        int y = player.getWorld().getHighestBlockYAt(x,z) + 2;

        Location location = new Location(player.getWorld(),x,y,z);
        player.teleport(location);
    }

    private int random(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

    public void start(int ticks) {
        if(runnable!=null) {
            stop();
        }

        this.players = Bukkit.getOnlinePlayers().iterator();
        this.delay = ticks;

        runnable = new BukkitRunnable() {

            private int tick = 0;

            @Override
            public void run() {
                tick++;

                if(!players.hasNext()) {
                    if(tick < delay)
                        return;

                    tick = 0;
                    players = Bukkit.getOnlinePlayers().iterator();
                }

                if(!players.hasNext())
                    return;

                Player player = players.next();
                RTP(player);
                player.sendMessage("Teleported!");
            }
        };

        runnable.runTaskTimer(StressTest.getInstance(),0,1);

    }

    public void stop() {
        if(runnable!=null && !runnable.isCancelled()) {
            runnable.cancel();
        }
    }

}
