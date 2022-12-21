package me.deltaorion.townymissionsv2.mission.reward.type;

import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.metadata.IntegerDataField;
import com.palmergames.bukkit.towny.object.metadata.StringDataField;
import me.deltaorion.townymissionsv2.TownyMissionsV2;
import me.deltaorion.townymissionsv2.configuration.Message;
import me.deltaorion.townymissionsv2.mission.reward.OfflineReward;
import me.deltaorion.townymissionsv2.mission.reward.OfflineRewardManager;
import me.deltaorion.townymissionsv2.player.MissionPlayer;
import me.deltaorion.townymissionsv2.util.ExperienceUtil;
import me.deltaorion.townymissionsv2.util.TownyUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

public class RewardListener implements Listener {

    private final TownyMissionsV2 plugin;

    public RewardListener(TownyMissionsV2 plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEvent(PlayerJoinEvent event) {
        List<OfflineReward> rewards = OfflineRewardManager.getAndRemove(event.getPlayer().getUniqueId());
        if(rewards==null)
            return;

        for(OfflineReward reward : rewards) {
            reward.handReward();
        }
    }

}
