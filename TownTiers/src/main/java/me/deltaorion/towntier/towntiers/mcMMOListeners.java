package me.deltaorion.towntier.towntiers;

import com.gmail.nossr50.events.experience.McMMOPlayerPreXpGainEvent;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Town;
import me.deltaorion.towntier.towntiers.townyutils.TownyUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class mcMMOListeners implements Listener {

    @EventHandler
    public void onMCMMO(McMMOPlayerPreXpGainEvent event) {
        Town town = TownyUtils.getTownFromPlayer(event.getPlayer());
        if(town==null) {
            return;
        }
        float sum = 0;
        TownTier tier = TownTiers.getInstance().getTierFromTown(town);
        sum += tier.getExtraMCMMOExperience();
        Nation nation = TownyUtils.getNationFromPlayer(event.getPlayer());
        if(nation!=null) {
            NationTier nTier = TownTiers.getInstance().getTierFromNation(nation);
            sum+=nTier.getExtraMCMMOExperience();
        }
        float m = 1+sum/100;
        event.setXpGained((int) (event.getXpGained()*m));
    }
}
