package me.cedric.cpslimiter;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class CPSLimiter extends JavaPlugin implements CommandExecutor {

    private PacketListener packetListener;

    @Override
    public void onEnable() {
        //getServer().getPluginManager().registerEvents(new PlayerInteractListener(this, 1), this);
        saveDefaultConfig();
        packetListener = new PacketListener(this, ListenerPriority.NORMAL, getConfig().getInt("max-cps"), PacketType.Play.Client.USE_ENTITY);
        ProtocolLibrary.getProtocolManager().addPacketListener(packetListener);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("cpslimiter.reload")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to do this");
            return true;
        }

        if (!args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage("/cpslimiter reload");
            return true;
        }

        reloadConfig();
        int maxCPS = getConfig().getInt("max-cps");

        packetListener.setMaxCPS(maxCPS);
        sender.sendMessage("Reloaded.");

        return true;
    }
}
