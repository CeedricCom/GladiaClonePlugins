package me.deltaorion.townymissionsv2.command.admin;

import com.palmergames.bukkit.towny.object.Government;
import me.deltaorion.townymissionsv2.TownyMissionsV2;
import me.deltaorion.townymissionsv2.bearer.MissionBearer;
import me.deltaorion.townymissionsv2.command.ArgumentList;
import me.deltaorion.townymissionsv2.command.CommandException;
import me.deltaorion.townymissionsv2.command.CommandPermissions;
import me.deltaorion.townymissionsv2.command.sub.MissionAdminCommand;
import me.deltaorion.townymissionsv2.util.DurationParser;
import me.deltaorion.townymissionsv2.util.TownyUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class MissionAdminCooldownAdd implements me.deltaorion.townymissionsv2.command.SubCommand {
    @Override
    public void onCommand(TownyMissionsV2 plugin, CommandSender sender, ArgumentList args) throws CommandException {
        Government government = args.getArgOrFail(0).parse(Government.class, MissionAdminCommand.GOV_PARSER);
        MissionBearer bearer = plugin.getMissionManager().getMissionBearer(government.getUUID());
        Duration duration = args.getArgOrFail(1).asDuration();
        bearer.setCooldown(duration);
        throw new CommandException("Successfully Added cooldown of "+ DurationParser.print(duration));
    }

    @Override
    public String getDescription() {
        return "Adds a cooldown to the government";
    }

    @Override
    public String getPermission() {
        return CommandPermissions.ADMIN_ADD_COOLDOWN.getPerm();
    }

    @Override
    public String getUsage() {
        return "/missions admin add-cooldown [town] [duration]";
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        if(args.length==1)
            return TownyUtil.getGovernmentTabCompletions();

        return new ArrayList<>();
    }
}
