package me.deltaorion.siegestats.controller.listener;

import com.gmail.goosius.siegewar.events.SiegeEndEvent;
import com.gmail.goosius.siegewar.events.SiegeWarStartEvent;
import me.deltaorion.siegestats.model.StatSiege;
import me.deltaorion.siegestats.service.SiegeService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class SiegeWarListener implements Listener {

    private final SiegeService siegeManager;

    public SiegeWarListener(SiegeService siegeManager) {
        this.siegeManager = siegeManager;
    }


    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSiege(SiegeWarStartEvent event) {
        siegeManager.createSiege(event.getSiege());
    }

    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEnd(SiegeEndEvent event) {
        StatSiege siege = siegeManager.getOrMakeTown(event.getSiege().getTown()).getLatestSiege();
        if(siege==null)
            return;

        siege.setVictor(event.getSiege().getSiegeWinner());
    }
}
