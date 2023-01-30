package me.cedric.cpslimiter;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedEnumEntityUseAction;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PacketListener extends PacketAdapter {

    private final Map<UUID, Long> tracker = new ConcurrentHashMap<>();
    private int maxCPS;

    public PacketListener(Plugin plugin, ListenerPriority listenerPriority, int maxCPS, PacketType... types) {
        super(plugin, listenerPriority, types);
        this.maxCPS = maxCPS;
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        WrappedEnumEntityUseAction useAction = event.getPacket().getEnumEntityUseActions().read(0);

        if (useAction.getAction() != EnumWrappers.EntityUseAction.ATTACK)
            return;

        Player player = event.getPlayer();
        float interval = 1000F / maxCPS;

        if (tracker.containsKey(player.getUniqueId())) {
            long lastClick = tracker.get(player.getUniqueId());
            if (System.currentTimeMillis() - lastClick < interval) {
                event.setCancelled(true);
                return;
            }
        }

        if (tracker.containsKey(player.getUniqueId()))
            tracker.replace(player.getUniqueId(), System.currentTimeMillis());
        else
            tracker.put(player.getUniqueId(), System.currentTimeMillis());
    }

    public void setMaxCPS(int maxCPS) {
        this.maxCPS = maxCPS;
    }
}
