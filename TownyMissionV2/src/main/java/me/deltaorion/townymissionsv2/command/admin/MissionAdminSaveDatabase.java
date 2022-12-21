package me.deltaorion.townymissionsv2.command.admin;

import com.palmergames.bukkit.towny.object.Government;
import me.deltaorion.townymissionsv2.TownyMissionsV2;
import me.deltaorion.townymissionsv2.bearer.MissionBearer;
import me.deltaorion.townymissionsv2.command.ArgumentList;
import me.deltaorion.townymissionsv2.command.CommandException;
import me.deltaorion.townymissionsv2.command.CommandPermissions;
import me.deltaorion.townymissionsv2.command.SubCommand;
import me.deltaorion.townymissionsv2.command.sub.MissionAdminCommand;
import me.deltaorion.townymissionsv2.util.TownyUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class MissionAdminSaveDatabase implements SubCommand {
    @Override
    public void onCommand(TownyMissionsV2 plugin, CommandSender sender, ArgumentList args) throws CommandException {
        long initialTime = System.currentTimeMillis();
        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getStorage().saveAll();
                sender.sendMessage("Successfully Saved Database");
                sender.sendMessage("Elapsed Time --- " + (System.currentTimeMillis()-initialTime));
            }
        }.runTaskAsynchronously(plugin);
    }

    @Override
    public String getDescription() {
        return "Saves the Missions Database";
    }

    @Override
    public String getPermission() {
        return CommandPermissions.DATABASE_SAVE.getPerm();
    }

    @Override
    public String getUsage() {
        return "/missions admin save-database";
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
