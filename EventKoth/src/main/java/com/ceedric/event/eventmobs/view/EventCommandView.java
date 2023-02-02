package com.ceedric.event.eventmobs.view;

import com.ceedric.event.eventmobs.model.BossWorld;
import com.ceedric.event.eventmobs.model.MythicKill;
import com.ceedric.event.eventmobs.model.participant.BossSide;
import com.ceedric.event.eventmobs.model.participant.Participant;
import me.deltaorion.common.plugin.sender.Sender;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class EventCommandView {

    private final DateFormat dmy = new SimpleDateFormat("dd/MM/yyyy");

    public void displayWorld(Sender sender, BossWorld world) {
        sender.sendMessage(ChatColor.YELLOW + "---oO Boss Stats Oo---");
        displayStat(sender,"Time",dmy.format(new Date(world.getEventStart())));

        for(MythicKill kill : world.getKills()) {
            displayKill(sender,world,kill);
        }
    }

    private void displayKill(Sender sender, BossWorld world,MythicKill kill) {
        Location location = kill.getLocation();
        ChatColor sideColor = ChatColor.GREEN;
        if(kill.getDeathSide().equals(BossSide.PLAYERS))
            sideColor = ChatColor.RED;

        sender.sendMessage("["+ getRelativeTime(world.getEventStart(),kill.getTime()) + "] [" + sideColor + kill.getDeathSide().getFormattedName() + " Death] " + ChatColor.GOLD +kill.getKiller().getName()+ChatColor.YELLOW+" killed "+ ChatColor.GOLD + kill.getVictim().getName()+ChatColor.YELLOW+" at "+"("+format(location.getX())+", "+format(location.getY())+", "+format(location.getZ())+")");
    }

    private String format(double val) {
        return String.format("%.2f",val);
    }

    private String getRelativeTime(long startEpoch, long finishEpoch) {
        LocalDateTime start = LocalDateTime.ofInstant(Instant.ofEpochMilli(startEpoch), ZoneId.systemDefault());
        LocalDateTime end = LocalDateTime.ofInstant(Instant.ofEpochMilli(finishEpoch), ZoneId.systemDefault());

        long minutes = ChronoUnit.MINUTES.between(start, end);

        return String.format("%02d", minutes);
    }

    private void displayStat(Sender sender, String title, String value) {
        sender.sendMessage(ChatColor.GOLD+title+": "+ChatColor.WHITE+value);
    }

    public void displayParticipant(Sender sender ,BossWorld world, Participant participant) {
        sender.sendMessage(ChatColor.YELLOW + "---oO "+ ChatColor.WHITE + participant.getName() + ChatColor.YELLOW + " Oo---");
        displayStat(sender,"Damage", String.valueOf(participant.getDamage()));
        int kills = 0;
        for(MythicKill kill : world.getKills()) {
            if(kill.getKiller().equals(participant)) {
                displayKill(sender,world,kill);
                kills++;
            }
        }

        displayStat(sender,"Total Kills",String.valueOf(kills));
    }

}
