package me.deltaorion.townymissionsv2.mission.chat;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.deltaorion.townymissionsv2.TownyMissionsV2;
import me.deltaorion.townymissionsv2.mission.Mission;
import me.deltaorion.townymissionsv2.mission.MissionGoal;
import me.deltaorion.townymissionsv2.mission.definition.GoalDefinition;
import me.deltaorion.townymissionsv2.player.MissionPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;

public class ChatListener implements Listener {

    private final TownyMissionsV2 plugin;
    private final GoalDefinition definition;

    public ChatListener(TownyMissionsV2 plugin, GoalDefinition definition) {
        this.plugin = plugin;
        this.definition = definition;
    }

    @EventHandler (priority = EventPriority.MONITOR,ignoreCancelled = false)
    public void onChat(AsyncPlayerChatEvent event) {
        MissionPlayer player = plugin.getPlayerManager().getPlayer(event.getPlayer());

        Mission primary = plugin.getMissionManager().getPrimaryMission(player,player.getContributePriority());

        if(primary!=null) {
            if (primary.getCurrentGoal() != null) {
                if (primary.getCurrentGoal().getDefinition().equals(definition)) {
                    if(contributeToGoal(player.getPlayer(), event.getMessage(), primary.getCurrentGoal()))
                        return;
                }
            }
        }

        List<MissionGoal> validGoals = plugin.getMissionManager().getActiveGoalsWithDefinition(player.getGovernmentIDs(),definition);
        for(MissionGoal goal : validGoals) {
            if(contributeToGoal(player.getPlayer(), event.getMessage(), goal))
                return;
        }
    }

    private boolean contributeToGoal(Player player, String message, MissionGoal goal) {
        ChatGoal chatGoal = (ChatGoal) goal;
        if(message.contains(chatGoal.getWord())) {
            chatGoal.contribute(player.getUniqueId(),1);
            return true;
        }
        return false;
    }
}
