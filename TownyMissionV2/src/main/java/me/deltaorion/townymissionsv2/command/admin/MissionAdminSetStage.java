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

import java.util.ArrayList;
import java.util.List;

public class MissionAdminSetStage implements me.deltaorion.townymissionsv2.command.SubCommand {
    @Override
    public void onCommand(TownyMissionsV2 plugin, CommandSender sender, ArgumentList args) throws CommandException {
        Government government = args.getArgOrFail(0).parse(Government.class, MissionAdminCommand.GOV_PARSER);
        MissionBearer bearer = plugin.getMissionManager().getMissionBearer(government.getUUID());
        if(bearer.getPrimaryMission()==null)
            throw new CommandException(ChatColor.RED+"Cannot change primary mission as there is none");

        int stage = args.getArgOrFail(1).asInt();
        bearer.getPrimaryMission().setStage(stage);
    }

    @Override
    public String getDescription() {
        return "Changes the bearers primary missions's current stage. Mission stages are from 0 to the amount of goals. This command is quite dodgy and will likely cause errors!";
    }

    @Override
    public String getPermission() {
        return CommandPermissions.ADMIN_SET_STAGE.getPerm();
    }

    @Override
    public String getUsage() {
        return "missions admin set-stage [town] [stage]";
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        if(args.length==1)
            return TownyUtil.getGovernmentTabCompletions();

        return new ArrayList<>();
    }
}
