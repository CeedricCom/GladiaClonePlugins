package me.deltaorion.townymissionsv2.player;

import me.deltaorion.townymissionsv2.configuration.Message;
import me.deltaorion.townymissionsv2.mission.Mission;

public class AutoContribute {

    private boolean toggled = false;
    private Mission mission;
    private final MissionPlayer player;

    public AutoContribute(MissionPlayer player) {
        this.player = player;
    }


    public boolean isToggled() {

        if(mission!=null) {
            if (!mission.getMissionBearer().contains(player)) {
                return false;
            }
        }

        return toggled;
    }

    public void setToggled(boolean toggled) {
        this.toggled = toggled;
    }

    public Mission getMission() {
        return mission;
    }

    public void setMission(Mission mission) {
        this.mission = mission;
    }

    public boolean toggledForAll() {
        return this.mission == null;
    }

    public void set(boolean toggle, Mission mission) {

        setMission(mission);
        setToggled(toggle);

        if(toggledForAll()) {
            player.getPlayer().sendMessage(Message.COMMAND_AUTOCONTRIBUTE_TOGGLE_ALL.getMessage(getToggledState(isToggled())));
        } else {
            player.getPlayer().sendMessage(Message.COMMAND_AUTOCONTRIBUTE_TOGGLE_SPECIFIC.getMessage(getToggledState(player.getAutoContribute().isToggled())));
        }
    }
    private String getToggledState(boolean toggled) {
        if(toggled) {
            return "on";
        } else {
            return "off";
        }
    }
}
