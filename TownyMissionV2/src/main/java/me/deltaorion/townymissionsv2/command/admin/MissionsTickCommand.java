package me.deltaorion.townymissionsv2.command.admin;

import com.google.common.collect.ImmutableList;
import me.deltaorion.townymissionsv2.TownyMissionsV2;
import me.deltaorion.townymissionsv2.command.ArgumentList;
import me.deltaorion.townymissionsv2.command.CommandException;
import me.deltaorion.townymissionsv2.command.CommandPermissions;
import org.bukkit.command.CommandSender;

import java.util.List;

public class MissionsTickCommand implements me.deltaorion.townymissionsv2.command.SubCommand {
    @Override
    public void onCommand(TownyMissionsV2 plugin, CommandSender sender, ArgumentList args) throws CommandException {
        if(args.getArgOrFail(0).asString().equalsIgnoreCase("start")) {
            if(plugin.getPool().running())
                throw new CommandException("Cannot start pool as it is already running!");

            plugin.getPool().start();
            throw new CommandException("Successfully Started Pool!");
        } else {
            if(!plugin.getPool().running())
                throw new CommandException("Cannot stop the pool as it is not running!");

            plugin.getPool().stop();
            throw new CommandException("Successfully stopped pool");
        }
    }

    @Override
    public String getDescription() {
        return "Allows one to start and stop the missions pool loop. (This is responsible for handing missions to towns)";
    }

    @Override
    public String getPermission() {
        return CommandPermissions.ADMIN_TICK.getPerm();
    }

    @Override
    public String getUsage() {
        return "/missions admin tick [start/stop]";
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        return ImmutableList.of("start","stop");
    }
}
