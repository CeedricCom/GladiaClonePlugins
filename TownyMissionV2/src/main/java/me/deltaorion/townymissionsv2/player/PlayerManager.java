package me.deltaorion.townymissionsv2.player;

import com.google.common.base.Preconditions;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {

    private final Map<UUID,MissionPlayer> playerMap;
    private final Plugin plugin;

    public PlayerManager(Plugin plugin) {

        Preconditions.checkNotNull(plugin);

        this.plugin = plugin;
        this.playerMap = new HashMap<>();
    }

    public MissionPlayer getPlayer(UUID uuid) {

        Preconditions.checkNotNull(uuid);

        if(playerMap.containsKey(uuid)) {
            return playerMap.get(uuid);
        } else {
            MissionPlayer missionPlayer = new MissionPlayer(uuid,plugin);
            playerMap.put(uuid,missionPlayer);
            return missionPlayer;
        }
    }

    public MissionPlayer getPlayer(Player player) {
        return getPlayer(player.getUniqueId());
    }

    public MissionPlayer getPlayer(CommandSender sender) {
        if(sender instanceof Player) {
            return getPlayer(((Player) sender).getUniqueId());
        }
        return null;
    }
}
