package me.deltaorion.siegewarschedule;

import com.gmail.goosius.siegewar.settings.SiegeWarSettings;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class WarScheduleCommand implements CommandExecutor {

    private final static Pattern COMMA = Pattern.compile(",");
    private final static DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("hh:mm a");

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        sender.sendMessage(ChatColor.GREEN +"---oO SiegeWar Schedule Oo---");
        sender.sendMessage(ChatColor.GOLD + "Battle Session Duration: " + ChatColor.WHITE +SiegeWarSettings.getWarSiegeBattleSessionsDurationMinutes() +"m");
        sender.sendMessage(ChatColor.GOLD + "Timezone: "+ChatColor.WHITE + "CET");
        String weekDaySessions = SiegeWarSettings.getWarSiegeBattleSessionWeekdayStartTimes();
        String weekEndSessions = SiegeWarSettings.getWarSiegeBattleSessionWeekendStartTimes();
        if (weekEndSessions.equals(weekDaySessions)) {
            sender.sendMessage(ChatColor.GOLD + "Battle Sessions");
            sender.sendMessage(getFormattedTimes(getTimes(weekDaySessions)));
        } else {
            sender.sendMessage(ChatColor.GOLD + "Weekday Sessions");
            sender.sendMessage(getFormattedTimes(getTimes(weekDaySessions)));
            sender.sendMessage(ChatColor.GOLD + "Weekend Sessions");
            sender.sendMessage(getFormattedTimes(getTimes(weekEndSessions)));
        }
        return true;
    }

    private String getFormattedTimes(List<LocalDateTime> times) {
        StringBuilder builder = new StringBuilder(ChatColor.GOLD.toString()).append('[').append(ChatColor.YELLOW);
        int count = 0;
        for(LocalDateTime time : times) {
            builder.append(ChatColor.YELLOW).append(time.atOffset(ZoneOffset.UTC).format(dateFormat));
            if(count<times.size()-1) {
                builder.append(ChatColor.WHITE).append(',').append(' ');
            }
            count++;
        }

        builder.append(ChatColor.GOLD).append(']');
        return builder.toString();
    }

    private List<LocalDateTime> getTimes(String timeString) {
        List<LocalDateTime> times = new ArrayList<>();

        LocalDate now = LocalDate.now();
        for(String time : COMMA.split(timeString)) {
            if (time.contains(":")) {
                String[] hourMinPair = time.split(":");
                times.add(LocalDateTime.of(now,LocalTime.of(Integer.parseInt(hourMinPair[0]), Integer.parseInt(hourMinPair[1]))));
            } else {
                times.add(LocalDateTime.of(now,LocalTime.of(Integer.parseInt(toString()), 0)));
            }
        }

        return times;
    }

}
