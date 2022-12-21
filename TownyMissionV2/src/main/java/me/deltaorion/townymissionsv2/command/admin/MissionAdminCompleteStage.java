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

public class MissionAdminCompleteStage implements me.deltaorion.townymissionsv2.command.SubCommand {
    @Override
    public void onCommand(TownyMissionsV2 plugin, CommandSender sender, ArgumentList args) throws CommandException {
        Government government = args.getArgOrFail(0).parse(Government.class, MissionAdminCommand.GOV_PARSER);
        MissionBearer bearer = plugin.getMissionManager().getMissionBearer(government.getUUID());
        if(bearer.getPrimaryMission()==null)
            throw new CommandException(ChatColor.RED+"Cannot Complete primary mission Goal as there is none");

        bearer.getPrimaryMission().getCurrentGoal().forceComplete();
        throw new CommandException("Successfully Completed Stage");
    }

    @Override
    public String getDescription() {
        return "Completes the current mission goal";
    }

    @Override
    public String getPermission() {
        return CommandPermissions.ADMIN_COMPLETE_GOAL.getPerm();
    }

    @Override
    public String getUsage() {
        return "/missions admin complete-current-goal [town]";
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        return TownyUtil.getGovernmentTabCompletions();
    }
}
