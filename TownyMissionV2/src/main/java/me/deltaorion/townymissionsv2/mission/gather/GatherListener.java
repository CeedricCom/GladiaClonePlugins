package me.deltaorion.townymissionsv2.mission.gather;

import com.google.common.collect.ImmutableList;
import me.deltaorion.townymissionsv2.TownyMissionsV2;
import me.deltaorion.townymissionsv2.mission.Mission;
import me.deltaorion.townymissionsv2.mission.MissionGoal;
import me.deltaorion.townymissionsv2.mission.definition.GoalDefinition;
import me.deltaorion.townymissionsv2.player.MissionPlayer;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GatherListener implements Listener {

    private final TownyMissionsV2 plugin;
    private final GoalDefinition definition;

    public GatherListener(TownyMissionsV2 plugin, GoalDefinition definition) {
        this.plugin = plugin;
        this.definition = definition;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPickup(PlayerAttemptPickupItemEvent event) {
        autoContribute(new PickupContributable(event));
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBreak(BlockDropItemEvent event) {
        autoContribute(new BreakContributable(event));
    }

    private void autoContribute(ContributableEvent event) {

        MissionPlayer player = plugin.getPlayerManager().getPlayer(event.getPlayer().getUniqueId());

        if (!player.getAutoContribute().isToggled())
            return;

        //select primary first
        Mission mission = player.getAutoContribute().getMission();


        List<MissionGoal> goals = new ArrayList<>();
        if (mission == null) {
            goals = plugin.getMissionManager().getActiveGoalsWithDefinition(player, definition);
        } else {

            if (mission.missionOver())
                return;

            if (mission.getCurrentGoal() != null) {
                if (mission.getCurrentGoal().getDefinition().equals(definition)) {
                    goals.add(mission.getCurrentGoal());
                }
            }

        }

        for (MissionGoal goal : goals) {
            GatherGoal gatherGoal = (GatherGoal) goal;
            Iterator<Item> iterator = event.getItems().iterator();
            while(iterator.hasNext()) {
                Item item = iterator.next();
                boolean destroy = gatherGoal.contribute(player, item);
                if (destroy) {
                    event.remove(item, iterator);
                }
            }
        }
    }

    public interface ContributableEvent {
        public void remove(Item item, Iterator<Item> iterator);

        public List<Item> getItems();

        public Player getPlayer();
    }

    public class PickupContributable implements ContributableEvent {

        private final PlayerAttemptPickupItemEvent event;

        public PickupContributable(PlayerAttemptPickupItemEvent event) {
            this.event = event;
        }

        @Override
        public void remove(Item item, Iterator<Item> iterator) {
            event.setCancelled(true);
            item.remove();
        }

        @Override
        public List<Item> getItems() {
            return ImmutableList.of(event.getItem());
        }

        @Override
        public Player getPlayer() {
            return event.getPlayer();
        }
    }

    public class BreakContributable implements ContributableEvent {

        private final BlockDropItemEvent event;

        public BreakContributable(BlockDropItemEvent event) {
            this.event = event;
        }

        @Override
        public void remove(Item item, Iterator<Item> iterator) {
            iterator.remove();
        }

        @Override
        public List<Item> getItems() {
            return event.getItems();
        }

        @Override
        public Player getPlayer() {
            return event.getPlayer();
        }
    }
}
