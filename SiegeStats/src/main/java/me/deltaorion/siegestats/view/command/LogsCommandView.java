package me.deltaorion.siegestats.view.command;

import me.deltaorion.common.plugin.sender.Sender;
import me.deltaorion.siegestats.model.SiegeKill;
import me.deltaorion.siegestats.model.StatSiege;
import me.deltaorion.siegestats.model.killer.Participant;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogsCommandView {

    public void displayKillLogs(Sender sender , StatSiege siege) {
        Map<Participant,KillPart> killParticipants = new HashMap<>();

        for(Participant participant  :siege.getParticipants()) {
            killParticipants.put(participant,new KillPart(participant));
        }

        for(SiegeKill kill : siege.getKills()) {
            KillPart killPart = killParticipants.get(kill.getKiller());
            if(killPart==null) {
                killPart = new KillPart(kill.getKiller());
                killParticipants.put(kill.getKiller(),killPart);
            }

            killPart.addKill();
        }

        List<KillPart> killStats = new ArrayList<>(killParticipants.values());
        killStats.sort((o1, o2) -> Integer.compare(o2.kills, o1.kills));

        for(KillPart part : killStats) {
            displayStat(sender,part.participant.getName(), String.valueOf(part.kills));
        }
    }

    public void displayDamageLogs(Sender sender, StatSiege siege) {
        List<Participant> participants = new ArrayList<>(siege.getParticipants());
        participants.sort((o1, o2) -> Double.compare(o2.getDamage(), o1.getDamage()));
        for(Participant participant : participants) {
            displayStat(sender,participant.getName(),format(participant.getDamage()));
        }
    }

    public void displayDeathsLogs(Sender sender, StatSiege siege) {
        Map<Participant,KillPart> killParticipants = new HashMap<>();

        for(Participant participant  :siege.getParticipants()) {
            killParticipants.put(participant,new KillPart(participant));
        }

        for(SiegeKill kill : siege.getKills()) {
            KillPart killPart = killParticipants.get(kill.getVictim());
            if(killPart==null) {
                killPart = new KillPart(kill.getKiller());
                killParticipants.put(kill.getKiller(),killPart);
            }

            killPart.addKill();
        }

        List<KillPart> killStats = new ArrayList<>(killParticipants.values());
        killStats.sort((o1, o2) -> Integer.compare(o2.kills, o1.kills));

        for(KillPart part : killStats) {
            displayStat(sender,part.participant.getName(), String.valueOf(part.kills));
        }
    }

    private void displayStat(Sender sender, String title, String value) {
        sender.sendMessage(ChatColor.GOLD+title+": "+ChatColor.WHITE+value);
    }

    private String format(double val) {
        return String.format("%.2f",val);
    }

    private static class KillPart {
        private final Participant participant;
        private int kills;

        private KillPart(Participant participant) {
            this.participant = participant;
            this.kills = 0;
        }

        private void addKill() {
            kills++;
        }
    }
}
