package me.deltaorion.siegestats.view.command;

import com.gmail.goosius.siegewar.enums.SiegeSide;
import com.gmail.goosius.siegewar.settings.SiegeWarSettings;
import com.palmergames.bukkit.towny.object.Town;
import me.deltaorion.common.plugin.sender.Sender;
import me.deltaorion.siegestats.model.SiegeKill;
import me.deltaorion.siegestats.model.SiegeTown;
import me.deltaorion.siegestats.model.StatSiege;
import me.deltaorion.siegestats.model.killer.Participant;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class SiegeCommandView {

    private final DateFormat dmy = new SimpleDateFormat("dd/MM/yyyy");

    public void displaySiegeOL(Sender sender, StatSiege siege) {
        sender.sendMessage(ChatColor.RED + getVsComponent(siege) + ChatColor.WHITE + ": "+dmy.format(new Date(siege.getStartTime())));
    }

    public void displayTown(Sender sender, SiegeTown town) {
        sender.sendMessage("Sieges on "+town.getLastName());
        int count = 1;
        for(StatSiege siege : town.getSieges()) {
            displaySiegeOL(sender,siege);
            count++;
        }
    }

    public void displaySiege(Sender sender, StatSiege siege) {
        sender.sendMessage(ChatColor.YELLOW + "---oO "+ getVsComponent(siege) + ChatColor.YELLOW + " Oo---");
        displayStat(sender,"Time",dmy.format(new Date(siege.getStartTime())));
        if(!siege.getVictor().equals(SiegeSide.NOBODY))
            displayStat(sender,"Victor",siege.getVictor().getFormattedName().defaultLocale());


        for(SiegeKill kill : siege.getKills()) {
            displayKill(sender,siege,kill);
        }
    }

    private void displayKill(Sender sender, StatSiege siege,SiegeKill kill) {
        Location location = kill.getLocation();
        ChatColor sideColor = ChatColor.GREEN;
        if(kill.getDeathSide().equals(SiegeSide.ATTACKERS))
            sideColor = ChatColor.RED;

        sender.sendMessage("["+ getRelativeTime(siege.getStartTime(),kill.getTime()) + "] [" + sideColor + kill.getDeathSide().getFormattedName().defaultLocale() +" Death]" + ChatColor.GOLD +kill.getKiller().getName()+ChatColor.YELLOW+" killed "+ ChatColor.GOLD + kill.getVictim().getName()+ChatColor.YELLOW+" at "+"("+format(location.getX())+", "+format(location.getY())+", "+format(location.getZ())+")");
    }

    private String format(double val) {
        return String.format("%.2f",val);
    }

    private String getRelativeTime(long startEpoch, long finishEpoch) {
        LocalDateTime start = LocalDateTime.ofInstant(Instant.ofEpochMilli(startEpoch), ZoneId.systemDefault());
        LocalDateTime end = LocalDateTime.ofInstant(Instant.ofEpochMilli(finishEpoch), ZoneId.systemDefault());

        long days = ChronoUnit.DAYS.between(start, end);
        long hours = ChronoUnit.HOURS.between(start, end) - (days * 24);
        long minutes = ChronoUnit.MINUTES.between(start, end) - (days * 24 * 60) - (hours * 60);

        return String.format("day %d, %02d:%02d", days+1, hours, minutes);
    }

    private void displayStat(Sender sender, String title, String value) {
        sender.sendMessage(ChatColor.GOLD+title+": "+ChatColor.WHITE+value);
    }

    private String getVsComponent(StatSiege siege) {
        return ChatColor.RED + siege.getInvaderName() + ChatColor.WHITE + " vs " + ChatColor.GREEN + siege.getBesieged().getLastName();
    }

    public void displayParticipant(Sender sender ,StatSiege siege, Participant participant) {
        sender.sendMessage(ChatColor.YELLOW + "---oO "+ ChatColor.WHITE + participant.getName() + ChatColor.YELLOW + " Oo---");
        displayStat(sender,"Damage", String.valueOf(participant.getDamage()));
        int kills = 0;
        for(SiegeKill kill : siege.getKills()) {
            if(kill.getKiller().equals(participant)) {
                kills++;
            }
        }

        displayStat(sender,"Total Kills",String.valueOf(kills));
        displayStat(sender,"Points Awarded",String.valueOf(kills * SiegeWarSettings.getWarBattlePointsForAttackerDeath()));

        for(SiegeKill kill : siege.getKills()) {
            if(kill.getKiller().equals(participant)) {
                displayKill(sender,siege,kill);
            }
        }
    }

    public void displayAll(Sender sender ,Collection<SiegeTown> towns) {
        sender.sendMessage("Displaying all sieges...");
        for(SiegeTown town : towns) {
            for(StatSiege siege : town.getSieges()) {
                displaySiegeOL(sender,siege);
            }
        }
    }
}
