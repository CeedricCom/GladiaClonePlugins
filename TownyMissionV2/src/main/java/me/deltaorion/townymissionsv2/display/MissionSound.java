package me.deltaorion.townymissionsv2.display;

import me.deltaorion.townymissionsv2.display.sound.SoundManager;
import me.deltaorion.townymissionsv2.player.MissionPlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public enum MissionSound {
    TEST("test.test"),
    MISSION_GOAL_COMPLETION("mission.goal.completion"),
    MISSION_AUTO_CONTRIBUTE_1("mission.goal.auto-contribute-1"),
    MISSION_AUTO_CONTRIBUTE_2("mission.goal.auto-contribute-2"),
    MISSION_AUTO_CONTRIBUTE_3("mission.goal.auto-contribute-3"),
    MISSION_COMPLETION("mission.mission.completion"),
    GUI_FAIL("gui.action.fail"),
    GUI_SUCCESS("gui.action.success")
    ;


    private final String location;

    MissionSound(String location) {
        this.location = location;
    }

    public MinecraftSound getSound() {
        return SoundManager.getSound(location);
    }

    public void playSound(MissionPlayer player) {
        player.playSound(getSound());
    }

    public void playSound(Player player) {
        getSound().playSound(player);
    }
}
