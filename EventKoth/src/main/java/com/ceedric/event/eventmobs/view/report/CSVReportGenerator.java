package com.ceedric.event.eventmobs.view.report;

import com.ceedric.event.eventmobs.model.BossWorld;
import com.ceedric.event.eventmobs.model.MythicKill;
import com.ceedric.event.eventmobs.model.participant.BossSide;
import com.ceedric.event.eventmobs.model.participant.Participant;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.bukkit.ChatColor;

import java.io.IOException;
import java.io.Writer;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSVReportGenerator implements ReportGenerator {
    @Override
    public void generate(Writer writer, BossWorld world) throws IOException {
        try (CSVPrinter printer = new CSVPrinter(writer, CSVFormat.EXCEL)) {
            printHeader(printer,world);
            printMainStatTable(printer,world);
            printKillOverTime(printer,world);
        }
    }

    private void printKillOverTime(CSVPrinter printer, BossWorld world) throws IOException {
        //defender death = attacker kill
        printer.println();
        printer.printRecord("Kills over time");
        printer.printRecord("time",BossSide.PLAYERS.getFormattedName(),BossSide.BOSS.getFormattedName());

        Map<Integer,Integer> attackerKills = new HashMap<>();
        Map<Integer,Integer> defenderKills = new HashMap<>();

        long maxTime = 0;
        for(MythicKill kill : world.getKills()) {
            int time = (int) getGraphTime(world.getEventStart(),kill.getTime());
            if(time > maxTime)
                maxTime = time;

            switch (opposite(kill.getDeathSide())) {
                case BOSS -> {
                    attackerKills.merge(time, 1, Integer::sum);
                } case PLAYERS -> {
                    defenderKills.merge(time, 1, Integer::sum);
                }
            }
        }

        for(int i=0;i<=maxTime;i++) {
            if(i>10000)
                break;

            printer.printRecord(i,attackerKills.getOrDefault(i,0),defenderKills.getOrDefault(i,0));
        }
    }

    private void printMainStatTable(CSVPrinter printer, BossWorld world) throws IOException {
        printer.printRecord("Participant Logs","","","","","","Kill Logs","","","","","");
        //participant logs sorted by damage
        printer.printRecord("Player","Damage","Kills","Deaths","KDR","","Time (h)","Killer Side","Killer","Victim","Location");
        Map<Participant,StatPlayer> players = new HashMap<>();

        List<List<String>> participantRows = new ArrayList<>();
        List<List<String>> killRows = new ArrayList<>();

        //add all the participants
        for(Participant participant : world.getParticipants()) {
            players.put(participant,new StatPlayer(participant));
        }

        //extract KD from each participant and log kills
        for(MythicKill kill : world.getKills()) {
            StatPlayer killer = players.get(kill.getKiller());
            StatPlayer victim = players.get(kill.getVictim());

            killer.kills++;
            victim.deaths++;

            String time = formatTime(world.getEventStart(),kill.getTime());

            List<String> row = new ArrayList<>();
            killRows.add(row);
            row.add(time);
            //attacker death = defender kill
            row.add(opposite(kill.getDeathSide()).getFormattedName());
            row.add(ChatColor.stripColor(kill.getKiller().getName()));
            row.add(ChatColor.stripColor(kill.getVictim().getName()));
            row.add("("+format(kill.getLocation().getX())+", "+format(kill.getLocation().getY())+", "+format(kill.getLocation().getZ())+")");
        }

        List<StatPlayer> statPlayers = new ArrayList<>(players.values());
        statPlayers.sort((o1, o2) -> Double.compare(o2.participant.getDamage(), o1.participant.getDamage()));

        for(StatPlayer player : statPlayers) {
            List<String> row = new ArrayList<>();
            participantRows.add(row);

            row.add(ChatColor.stripColor(player.participant.getName()));
            row.add(format(player.participant.getDamage()));
            row.add(String.valueOf(player.kills));
            row.add(String.valueOf(player.deaths));
            row.add(format(getKDR(player.kills,player.deaths)));
        }

        int i = 0;
        int j = 0;
        while (i < participantRows.size() || j < killRows.size()) {
            List<String> row = new ArrayList<>();
            if(i>=participantRows.size()) {
                for(int x=0;x<5;x++)
                    row.add("");

            } else {
                row.addAll(participantRows.get(i));
            }

            row.add("");

            if(j>=killRows.size()) {
                for(int x=0;x<5;x++)
                    row.add("");
            } else {
                row.addAll(killRows.get(j));
            }
            i++;
            j++;

            printer.printRecord(row);
        }
    }

    private BossSide opposite(BossSide deathSide) {
        if(deathSide.equals(BossSide.PLAYERS))
            return BossSide.BOSS;

        if(deathSide.equals(BossSide.BOSS))
            return BossSide.PLAYERS;

        return BossSide.NOBODY;
    }

    private void printHeader(CSVPrinter printer, BossWorld world) throws IOException {

        printer.printRecord("Aliens vs Humans");
        printer.println();

        printer.printRecord("Attacker",BossSide.BOSS.getFormattedName());
        printer.printRecord("Defender",BossSide.PLAYERS.getFormattedName());
        printer.printRecord("Winner",BossSide.PLAYERS.getFormattedName());
        printer.println();
    }

    private double getKDR(int kills, int deaths) {
        if(deaths==0)
            deaths=1;

        return kills / (double) deaths;
    }

    private String format(double val) {
        return String.format("%.2f",val);
    }

    private String formatTime(long startEpoch, long endEpoch) {
        LocalDateTime start = LocalDateTime.ofInstant(Instant.ofEpochMilli(startEpoch), ZoneId.systemDefault());
        LocalDateTime end = LocalDateTime.ofInstant(Instant.ofEpochMilli(endEpoch), ZoneId.systemDefault());

        long hours = ChronoUnit.HOURS.between(start, end);
        return String.valueOf(hours);
    }

    private long getGraphTime(long startEpoch, long endEpoch) {
        LocalDateTime start = LocalDateTime.ofInstant(Instant.ofEpochMilli(startEpoch), ZoneId.systemDefault());
        LocalDateTime end = LocalDateTime.ofInstant(Instant.ofEpochMilli(endEpoch), ZoneId.systemDefault());

        return ChronoUnit.MINUTES.between(start, end);
    }

    private static class StatPlayer {
        private final Participant participant;
        private int kills;
        private int deaths;

        private StatPlayer(Participant participant) {
            this.participant = participant;
        }


    }
}
