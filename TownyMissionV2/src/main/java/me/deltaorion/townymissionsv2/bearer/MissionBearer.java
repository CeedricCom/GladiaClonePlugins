package me.deltaorion.townymissionsv2.bearer;

import me.deltaorion.townymissionsv2.configuration.Message;
import me.deltaorion.townymissionsv2.display.MinecraftSound;
import me.deltaorion.townymissionsv2.mission.Mission;
import me.deltaorion.townymissionsv2.mission.MissionGoal;
import me.deltaorion.townymissionsv2.mission.definition.GoalDefinition;
import me.deltaorion.townymissionsv2.player.MissionPlayer;
import me.deltaorion.townymissionsv2.storage.Saveable;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface MissionBearer {

    Mission getPrimaryMission();

    public void setPrimaryMission(UUID selection);

    public void addMission(Mission mission);

    public void addPrimaryMission(Mission mission);

    public Collection<Mission> getMissions();

    public UUID getUniqueID();

    public boolean hasMission();

    public List<MissionGoal> getActiveGoalsWithDefinition(GoalDefinition definition);

    public void sendMessage(String text);

    public void playSound(MinecraftSound sound);

    public String getCompletionMessage();

    public boolean shouldBroadcast();

    void sendTitle(String missionGoalCompleteTitle, String message);

    String getName();

    boolean onCooldown();

    void setCooldown(Duration duration);

    Duration getCooldown();

    long getCooldownStart();

    boolean contains(MissionPlayer player);
}
