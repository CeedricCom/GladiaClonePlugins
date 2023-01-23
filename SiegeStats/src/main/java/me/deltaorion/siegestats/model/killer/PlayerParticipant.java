package me.deltaorion.siegestats.model.killer;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerParticipant extends Participant {

    public PlayerParticipant(Player player) {
        super(player.getUniqueId());
    }

    public PlayerParticipant(UUID playerId) {
        super(playerId);
    }

    @Override
    public String getName() {
        OfflinePlayer player = Bukkit.getOfflinePlayer(getUniqueId());
        if(player.getName()==null)
            return "error";

        return player.getName();
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(getUniqueId());
    }

    @Override
    public PlayerParticipant clone() {
        PlayerParticipant participant = new PlayerParticipant(getUniqueId());
        participant.addDamage(this.getDamage());
        return participant;
    }
}
