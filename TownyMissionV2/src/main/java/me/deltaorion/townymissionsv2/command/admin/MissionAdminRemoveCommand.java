package me.deltaorion.townymissionsv2.command.admin;

import com.palmergames.bukkit.towny.object.Government;
import me.deltaorion.townymissionsv2.TownyMissionsV2;
import me.deltaorion.townymissionsv2.bearer.MissionBearer;
import me.deltaorion.townymissionsv2.command.ArgumentList;
import me.deltaorion.townymissionsv2.command.CommandException;
import me.deltaorion.townymissionsv2.command.CommandPermissions;
import me.deltaorion.townymissionsv2.command.sub.MissionAdminCommand;
import me.deltaorion.townymissionsv2.util.TownyUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class MissionAdminRemoveCommand implements me.deltaorion.townymissionsv2.command.SubCommand {
    @Override
    public void onCommand(TownyMissionsV2 plugin, CommandSender sender, ArgumentList args) throws CommandException {
        Government government = args.getArgOrFail(0).parse(Government.class, MissionAdminCommand.GOV_PARSER);
        MissionBearer bearer = plugin.getMissionManager().getMissionBearer(government.getUUID());
        if(bearer.getPrimaryMission()==null)
            throw new CommandException(ChatColor.RED+"Cannot remove primary mission as there is none");

        bearer.getMissions().remove(bearer.getPrimaryMission());
        throw new CommandException("Successfully removed primary mission");
    }

    @Override
    public String getDescription() {
        return "Removes the primary mission of the government";
    }

    @Override
    public String getPermission() {
        return CommandPermissions.ADMIN_REMOVE_MISSION.getPerm();
    }

    @Override
    public String getUsage() {
        return "/missions admin remove-mission [government]";
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        return TownyUtil.getGovernmentTabCompletions();
    }
}
