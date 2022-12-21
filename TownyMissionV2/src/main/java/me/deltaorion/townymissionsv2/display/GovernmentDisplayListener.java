package me.deltaorion.townymissionsv2.display;

import com.palmergames.adventure.text.Component;
import com.palmergames.bukkit.towny.event.statusscreen.NationStatusScreenEvent;
import com.palmergames.bukkit.towny.event.statusscreen.TownStatusScreenEvent;
import com.palmergames.bukkit.towny.object.statusscreens.StatusScreen;
import me.deltaorion.townymissionsv2.TownyMissionsV2;
import me.deltaorion.townymissionsv2.mission.Mission;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class GovernmentDisplayListener implements Listener {

    private final TownyMissionsV2 plugin;

    public GovernmentDisplayListener(TownyMissionsV2 plugin) {
        this.plugin = plugin;
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onTownScreen(TownStatusScreenEvent event) {
        List<String> additionalLines = GovernmentScreenDisplay.getScreenText(plugin.getMissionManager().getMissionBearer(event.getTown().getUUID()),"Town");
        if(additionalLines.size()>0) {
            event.addLine("");
            event.addLines(additionalLines);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onNationScreen(NationStatusScreenEvent event) {
        List<String> additionalLines = GovernmentScreenDisplay.getScreenText(plugin.getMissionManager().getMissionBearer(event.getNation().getUUID()),"Nation");
        if(additionalLines.size()>0) {
            event.addLine("");
            event.addLines(additionalLines);
        }
    }
}
