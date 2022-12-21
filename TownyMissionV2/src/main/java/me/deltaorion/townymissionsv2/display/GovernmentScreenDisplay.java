package me.deltaorion.townymissionsv2.display;

import me.deltaorion.townymissionsv2.bearer.MissionBearer;
import me.deltaorion.townymissionsv2.configuration.Message;
import me.deltaorion.townymissionsv2.mission.Mission;
import me.deltaorion.townymissionsv2.mission.MissionGoal;
import me.deltaorion.townymissionsv2.mission.reward.AbstractReward;
import me.deltaorion.townymissionsv2.mission.reward.MissionReward;
import me.deltaorion.townymissionsv2.util.DurationParser;
import org.bukkit.ChatColor;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GovernmentScreenDisplay {

    private static final String PREFIX = ChatColor.DARK_GREEN + " > ";

    public static List<String> getScreenText(MissionBearer bearer, String govType) {

        if(bearer==null)
            return Collections.emptyList();

        if(bearer.getPrimaryMission()==null) {
            return displayNoMission(bearer,govType);
        }

        return displayMission(bearer.getPrimaryMission(),govType);
    }

    private static List<String> displayNoMission(MissionBearer bearer, String govType) {
        List<String> lines = new ArrayList<>(getHeader(govType));
        if(bearer.onCooldown()) {
            lines.add(PREFIX + Message.GOVERNMENT_COOLDOWN_SCREEN.getMessage(getTimeLeft(bearer.getCooldown(), bearer.getCooldownStart())));
        }

        return lines;
    }

    public static List<String> getHeader(String govType) {
        List<String> titles = new ArrayList<>();
        titles.add(Message.GOV_MISSION_TITLE.getMessage("None"));
        titles.add(PREFIX + Message.GOV_MISSION_HELP.getMessage(govType,govType));
        return titles;
    }

    public static List<String> displayMission(Mission mission, String govType) {
        //Mission: Status
        //   > help text
        //   > goal-reward
        //   > goal
        //   > duration (if applicable)

        List<String> titles = new ArrayList<>();

        titles.add(Message.GOV_MISSION_TITLE.getMessage(getStatus(mission)));
        titles.add(PREFIX + Message.GOV_MISSION_HELP.getMessage(govType,govType.toLowerCase()));

        if(hasRewards(mission)) {
            titles.add(PREFIX + getRewardText(mission));
        }

        if(mission.getCurrentGoal() != null) {
            titles.add(PREFIX + mission.getCurrentGoal().getDisplayText() + ": " + ChatColor.GREEN + mission.getCurrentGoal().getProgress());
        }

        if(!mission.getDuration().equals(Mission.INFINITE_DURATION) && !mission.missionExpired()) {
            titles.add(PREFIX + Message.GOV_DURATION.getMessage(getTimeLeft(mission)));
        }

        return titles;
    }

    private static String getStatus(Mission mission) {
        if(mission.missionOver()) {
            return "Complete";
        } else {
            return "In Progress";
        }
    }

    private static String getTimeLeft(Mission mission) {
        return getTimeLeft(mission.getDuration(),mission.getStartTime());
    }

    private static String getTimeLeft(Duration duration, long startTime) {
        long finishTime = duration.toMillis() + startTime;
        long timeLeft = finishTime - System.currentTimeMillis();

        return DurationParser.print(Duration.of(timeLeft, ChronoUnit.MILLIS));
    }

    private static boolean hasRewards(Mission mission) {
        return getRewardList(mission).size()>0 && !mission.missionOver();
    }

    private static String getRewardText(Mission mission) {

        StringBuilder text = new StringBuilder();
        List<AbstractReward> totalRewards = getRewardList(mission);

        for(int i=0;i<totalRewards.size();i++) {
            AbstractReward reward = totalRewards.get(i);
            text.append(getRewardSpash(reward));
            if(i<totalRewards.size()-1) {
                text.append(" | ");
            }
        }

        return text.toString();
    }

    private static List<AbstractReward> getRewardList(Mission mission) {
        List<AbstractReward> totalRewards = new ArrayList<>();
        totalRewards.addAll(mission.getRewards());

        if(mission.getCurrentGoal()!=null) {
            totalRewards.addAll(mission.getCurrentGoal().getRewards());
        }

        return totalRewards;
    }

    private static String getRewardSpash(AbstractReward abstractReward) {
        return abstractReward.getRewardName() + " - " + abstractReward.getTotal();
    }

}
