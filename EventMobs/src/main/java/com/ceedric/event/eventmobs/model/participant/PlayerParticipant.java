package com.ceedric.event.eventmobs.model.participant;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerParticipant extends Participant {


    public PlayerParticipant(Player player) {
        super(player.getUniqueId());
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(getUniqueId());
    }

    @Override
    public String getName() {
        OfflinePlayer player = Bukkit.getOfflinePlayer(getUniqueId());
        if(player.getName()==null)
            return "";

        return player.getName();
    }
}
