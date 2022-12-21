package me.deltaorion.townymissionsv2.bearer;

import me.deltaorion.townymissionsv2.mission.Mission;
import me.deltaorion.townymissionsv2.mission.MissionGoal;
import me.deltaorion.townymissionsv2.mission.definition.GoalDefinition;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.Duration;
import java.util.*;

public abstract class AbstractMissionBearer implements MissionBearer {

    private UUID primaryMission;
    private final Map<UUID,Mission> missions;
    private Duration missionCooldown;
    private long cooldownStart = 0;


    public AbstractMissionBearer() {
        this.missions = new HashMap<>();
    }

    @Nullable
    public Mission getPrimaryMission() {
        if(primaryMission==null) {
            findPrimaryMission();

            if(primaryMission == null) {
                return null;
            }
        }

        Mission mission = missions.get(primaryMission);
        if(mission == null || mission.missionOver()) {
            findPrimaryMission();

            mission = missions.get(primaryMission);
        }

        return mission;
    }

    private void findPrimaryMission() {
        boolean found = false;
        for(Mission mission : missions.values()) {
            if(!mission.missionOver()) {
                primaryMission = mission.getUniqueID();
                found = true;
            }
        }

        if(!found) {
            primaryMission = null;
        }
    }

    @Override
    public void setPrimaryMission(UUID selection) {
        this.primaryMission = selection;
    }

    public void addMission(Mission mission) {
        putMission(mission);
    }

    public void addPrimaryMission(Mission mission) {
        putMission(mission);
        this.primaryMission = mission.getUniqueID();
    }

    private void putMission(Mission mission) {
        this.missions.put(mission.getUniqueID(),mission);
    }

    public Collection<Mission> getMissions() {
        return missions.values();
    }

    public boolean hasMission() {
        return missions.size()>0;
    }


    public List<MissionGoal> getActiveGoalsWithDefinition(GoalDefinition definition) {
        if(missions.isEmpty())
            return Collections.emptyList();

        List<MissionGoal> goals = new ArrayList<>();
        for(Mission mission : getMissions()) {
            if(mission.getCurrentGoal()!=null &&
                    !mission.missionExpired() &&
                    !mission.missionOver()) {


                if(mission.getCurrentGoal().getDefinition().equals(definition) &&
                        !mission.getCurrentGoal().isComplete()) {
                    goals.add(mission.getCurrentGoal());
                }
            }
        }
        return goals;
    }

    public String toString() {
        return "Mission-UUID: " + getUniqueID() + System.lineSeparator() +
                "Primary-Mission: "+getPrimaryMission() + System.lineSeparator() +
                "Missions: "+getMissions() + System.lineSeparator();
    }

    @Override
    public boolean onCooldown() {
        if(missionCooldown==null)
            return false;

        long missionEnd = cooldownStart + missionCooldown.toMillis();
        return System.currentTimeMillis() <= missionEnd;
    }

    @Override
    public void setCooldown(Duration duration) {
        this.cooldownStart = System.currentTimeMillis();
        this.missionCooldown = duration;
    }

    protected void setMissionCooldown(Duration missionCooldown) {
        this.missionCooldown = missionCooldown;
    }

    protected void setCooldownStart(long cooldownStart) {
        this.cooldownStart = cooldownStart;
    }

    @Override
    public Duration getCooldown() {
        return missionCooldown;
    }

    @Override
    public long getCooldownStart() {
        return cooldownStart;
    }

}
