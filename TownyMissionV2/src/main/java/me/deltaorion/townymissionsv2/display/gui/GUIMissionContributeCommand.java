package me.deltaorion.townymissionsv2.display.gui;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import me.deltaorion.townymissionsv2.bearer.MissionBearer;
import me.deltaorion.townymissionsv2.mission.Mission;
import me.deltaorion.townymissionsv2.mission.MissionGoal;
import me.deltaorion.townymissionsv2.mission.goal.ContributableGoal;
import me.deltaorion.townymissionsv2.player.MissionPlayer;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Objects;
import java.util.function.Consumer;

import static me.deltaorion.townymissionsv2.display.gui.GUIManager.getMissionDisplayButton;

public class GUIMissionContributeCommand implements Consumer<InventoryClickEvent> {

    private final Mission mission;
    private final MissionPlayer player;
    private final StaticPane update;
    private final ChestGui gui;
    private final MissionBearer bearer;

    public GUIMissionContributeCommand(Mission mission, MissionPlayer player, StaticPane update, ChestGui gui, MissionBearer bearer) {
        this.mission = mission;
        this.player = player;
        this.update = update;
        this.gui = gui;
        this.bearer = bearer;
    }

    @Override
    public void accept(InventoryClickEvent event) {
        if(mission.missionOver())
            return;

        if(mission.getCurrentGoal()==null)
            return;

        if(mission.getCurrentGoal() instanceof ContributableGoal) {
            ContributableGoal goal = (ContributableGoal) mission.getCurrentGoal();
            int progress = mission.getCurrentGoal().getProgress();
            MissionGoal oldGoal = mission.getCurrentGoal();
            goal.contribute(player);

            boolean progressCondition = false;
            if(mission.getCurrentGoal()!=null)
                progressCondition = mission.getCurrentGoal().getProgress()!=progress;

            if(!Objects.equals(oldGoal,mission.getCurrentGoal()) || progressCondition) {
                update.addItem(getMissionDisplayButton(player,mission,update,gui,bearer),0,2);
                gui.update();
            }
        }
    }
}
