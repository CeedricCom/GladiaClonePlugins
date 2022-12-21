package me.deltaorion.townymissionsv2;

import com.google.common.base.Preconditions;
import com.palmergames.bukkit.towny.object.Government;
import me.deltaorion.townymissionsv2.bearer.GovernmentMissionBearer;
import me.deltaorion.townymissionsv2.bearer.MissionBearer;
import me.deltaorion.townymissionsv2.mission.Mission;
import me.deltaorion.townymissionsv2.mission.MissionGoal;
import me.deltaorion.townymissionsv2.mission.definition.GoalDefinition;
import me.deltaorion.townymissionsv2.player.ContributeType;
import me.deltaorion.townymissionsv2.player.MissionPlayer;
import me.deltaorion.townymissionsv2.util.TownyUtil;

import java.util.*;

public class MissionManager {

    private final Map<UUID, MissionBearer> townyMissions;

    public MissionManager() {
        this.townyMissions = new HashMap<>();
    }

    public MissionBearer getMissionBearer(UUID uuid) {

        Preconditions.checkNotNull(uuid);

        if(townyMissions.containsKey(uuid)) {
            return townyMissions.get(uuid);
        } else {
            MissionBearer bearer = createBearer(uuid);
            this.townyMissions.put(uuid,bearer);

            return bearer;
        }
    }

    private MissionBearer createBearer(UUID uuid) {
        Government government = TownyUtil.getGovernment(uuid);
        if(government==null)
            throw new IllegalArgumentException("UUID does not correspond to a valid towny object!");

        return new GovernmentMissionBearer(government);
    }

    public Collection<Mission> getMissions(UUID uuid) {
        Preconditions.checkNotNull(uuid);

        return getMissionBearer(uuid).getMissions();
    }

    public List<Mission> getMissions(MissionPlayer player) {
        List<Mission> missions = new ArrayList<>();
        for(UUID government : player.getGovernmentIDs()) {
            missions.addAll(getMissions(government));
        }
        return missions;
    }

    public Mission getMission(UUID governmentUUID, UUID missionUUID) {

        Preconditions.checkNotNull(governmentUUID);
        Preconditions.checkNotNull(missionUUID);

        Collection<Mission> missions = getMissions(governmentUUID);
        if(missions==null)
            return null;

        for(Mission mission : missions) {
            if(mission.getUniqueID().equals(missionUUID))
                return mission;
        }

        return null;
    }

    public List<MissionGoal> getActiveGoalsWithDefinition(MissionPlayer player, GoalDefinition definition) {
        Preconditions.checkNotNull(player);
        Preconditions.checkNotNull(definition);

        return getActiveGoalsWithDefinition(player.getGovernmentIDs(),definition);
    }

    public List<MissionGoal> getActiveGoalsWithDefinition(List<UUID> bearers, GoalDefinition definition) {

        Preconditions.checkNotNull(bearers);
        Preconditions.checkNotNull(definition);

        List<MissionGoal> goals = new ArrayList<>();

        for(UUID government: bearers) {
            goals.addAll(getActiveGoalsWithDefinition(government,definition));
        }

        return goals;
    }

    public List<MissionGoal> getActiveGoalsWithDefinition(UUID bearerUUID, GoalDefinition definition) {

        Preconditions.checkNotNull(definition);
        Preconditions.checkNotNull(bearerUUID);

        return getMissionBearer(bearerUUID).getActiveGoalsWithDefinition(definition);
    }

    public boolean hasMission(UUID missionBearer) {

        Preconditions.checkNotNull(missionBearer);

        return getMissionBearer(missionBearer).hasMission();
    }

    public boolean hasMission(MissionPlayer player) {

        Preconditions.checkNotNull(player);

        for(UUID uuid : player.getGovernmentIDs()) {
            if(hasMission(uuid)) {
                return true;
            }
        }
        return false;
    }


    public void registerMission(Mission mission) {

        Preconditions.checkNotNull(mission);

        UUID uuid = mission.getMissionBearer().getUniqueID();
        MissionBearer bearer = getMissionBearer(uuid);
        bearer.addMission(mission);
        mission.start();
    }

    public void registerPrimaryMission(Mission mission) {
        Preconditions.checkNotNull(mission);
        getMissionBearer(mission.getUniqueID()).addPrimaryMission(mission);
        mission.start();
    }

    public Mission getPrimaryMission(MissionPlayer player, ContributeType type) {
        if(type.equals(ContributeType.TOWN)) {
            return getPrimaryTownMission(player);
        } else if(type.equals(ContributeType.NATION)) {
            return getPrimaryNationMission(player);
        }

        return null;
    }

    private Mission getPrimaryTownMission(MissionPlayer player) {
        if(player.getTown()!=null) {
            return getMissionBearer(player.getTown().getUUID()).getPrimaryMission();
        }

        return null;
    }

    private Mission getPrimaryNationMission(MissionPlayer player) {
        if(player.getNation()!=null) {
            return getMissionBearer(player.getNation().getUUID()).getPrimaryMission();
        }

        return null;
    }

    public void loadBearer(MissionBearer bearer) {
        Preconditions.checkNotNull(bearer);
        this.townyMissions.put(bearer.getUniqueID(),bearer);
    }


    public String toString() {
        return townyMissions.toString();
    }

    public Collection<MissionBearer> getMissionBearers() {
        return townyMissions.values();
    }
}
