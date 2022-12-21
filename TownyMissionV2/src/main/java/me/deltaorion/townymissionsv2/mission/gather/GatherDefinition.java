package me.deltaorion.townymissionsv2.mission.gather;

import me.deltaorion.townymissionsv2.TownyMissionsV2;
import me.deltaorion.townymissionsv2.mission.definition.AbstractDefinition;

public class GatherDefinition extends AbstractDefinition {

    public GatherDefinition(TownyMissionsV2 plugin) {
        super(plugin, "Gather");
    }

    @Override
    public void onLoad() {
        getPlugin().getServer().getPluginManager().registerEvents(new GatherListener(getPlugin(),this),getPlugin());
    }

    @Override
    public void onDisable() {

    }
    

}
