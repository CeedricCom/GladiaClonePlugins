package com.ceedric.event.eventmobs.model.koth;

import com.ceedric.event.eventmobs.Permissions;
import com.ceedric.event.eventmobs.model.Event;
import com.ceedric.event.eventmobs.model.EventKill;
import com.ceedric.event.eventmobs.model.Side;
import com.ceedric.event.eventmobs.model.participant.Participant;
import com.ceedric.event.eventmobs.model.participant.PlayerParticipant;
import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.party.PartyManager;
import me.deltaorion.common.util.DurationParser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.*;

public class KothEvent extends Event {

    private final Party NOBODY = new Party("Other");
    private List<String> startCommands;
    private Duration respawnCooldown;
    private final Map<UUID,Long> respawnMap;
    private int playerCap;
    private Party victor;

    public KothEvent(String name, World world) {
        super(name, world);
        victor = NOBODY;
        this.respawnMap = new HashMap<>();
        this.startCommands = new ArrayList<>();
    }

    @Override
    protected void clearSpecific() {
        startCommands.clear();
    }

    @Override
    protected Event cloneSpecific() {
        KothEvent event = new KothEvent(getName(),getWorld());
        return event;
    }

    @Override
    public void recordKill(Location location, Participant killer, Participant victim) {
        Side side = getSide(killer,victim);
        if(side==null)
            return;

        if(victim instanceof PlayerParticipant player) {
            respawnMap.put(player.getUniqueId(),System.currentTimeMillis());
        }

        addKill(new EventKill(location,killer,victim,side));
    }

    @Override
    public void recordDamage(Participant damager, Participant damaged, Location location, double damage) {
        Side side = getSide(damager,damaged);
        if(side==null)
            return;

        damager.addDamage(damage);
    }

    private Side getSide(Participant killer, Participant victim) {
        Party killerParty = getParty(killer);
        Party victimParty = getParty(victim);

        if(victimParty == null)
            return null;

        if(killerParty == null)
            killerParty = NOBODY;

        return new KothSide(victimParty,killerParty);
    }

    private Party getParty(Participant participant) {
        if(!(participant instanceof PlayerParticipant player))
            return null;

        if(player.getPlayer()==null)
            return null;

        return PartyManager.getParty(player.getPlayer());
    }

    @Override
    public Collection<Participant> getRewardableParticipants() {
        List<Participant> participants = new ArrayList<>();
        for(Participant participant : getParticipants()) {
            if(participant instanceof PlayerParticipant player) {
                if(PartyManager.getParty(player.getPlayer()).equals(victor)) {
                    participants.add(participant);
                }
            }
        }

        participants.sort((o1, o2) -> Double.compare(o2.getDamage(),o1.getDamage()));
        return participants;
    }

    @Override
    public void startEvent() {
        victor = NOBODY;
        respawnMap.clear();

        for(String startCommand : startCommands) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), startCommand);
        }
    }

    @Override
    public Side getWinner() {
        return new KothSide(victor,victor);
    }

    @Override
    public boolean spawn(Player player) {
        if(player.hasPermission(Permissions.BYPASS)) {
            teleport(player,getSpawnLocation());
            return true;
        }

        if(respawnMap.containsKey(player.getUniqueId()))  {
            long cooldownStart = respawnMap.get(player.getUniqueId());
            long passed = System.currentTimeMillis() - cooldownStart;
            if(passed < respawnCooldown.toMillis()) {
                long toGo = cooldownStart + respawnCooldown.toMillis() - System.currentTimeMillis();
                player.sendMessage(ChatColor.RED+"You must wait "+ DurationParser.print(Duration.ofMillis(toGo))+" to respawn!");
                return false;
            }
        }

        Party party = PartyManager.getParty(player);
        if(party==null) {
            player.sendMessage(ChatColor.RED+"You must be in a party to join the koth");
            return false;
        }

        if(party.getAlly()!=null) {
            player.sendMessage(ChatColor.RED + "You may not join the koth as your party has an alliance with '"+party.getAlly().getName()+"'");
            return false;
        }

        if(party.getMembers().size()>playerCap) {
            player.sendMessage(ChatColor.RED + "You may not join the koth as your party has more than '"+playerCap+"' members");
            return false;
        }

        teleport(player,getSpawnLocation());
        return true;
    }

    private void teleport(Player player, Location spawnLocation) {
        player.teleport(spawnLocation);
    }

    public void recordWinner(Player capper) {
        Party party = PartyManager.getParty(capper);
        if(party==null) {
            Bukkit.getLogger().severe("Winner is not in a party");
            return;
        }

        this.victor = party;
    }

    public void addStartCommand(String startCommand) {
        this.startCommands.add(startCommand);
    }

    public void setRespawnCooldown(Duration respawnCooldown) {
        this.respawnCooldown = respawnCooldown;
    }

    public void setPlayerCap(int playerCap) {
        this.playerCap = playerCap;
    }

    public Collection<String> getStartCommands() {
        return startCommands;
    }

    public Duration getRespawnCooldown() {
        return respawnCooldown;
    }

    public int getPlayerCap() {
        return playerCap;
    }

}
