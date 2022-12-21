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
import org.bukkit.command.CommandSender;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class MissionAdminCooldownRemove implements me.deltaorion.townymissionsv2.command.SubCommand {
    @Override
    public void onCommand(TownyMissionsV2 plugin, CommandSender sender, ArgumentList args) throws CommandException {
        Government government = args.getArgOrFail(0).parse(Government.class, MissionAdminCommand.GOV_PARSER);
        MissionBearer bearer = plugin.getMissionManager().getMissionBearer(government.getUUID());
        bearer.setCooldown(Duration.of(0, ChronoUnit.MILLIS));
        throw new CommandException("Successfully Removed cooldown");
    }

    @Override
    public String getDescription() {
        return "Removes a cooldown from the government";
    }

    @Override
    public String getPermission() {
        return CommandPermissions.ADMIN_REMOVE_COOLDOWN.getPerm();
    }

    @Override
    public String getUsage() {
        return "/missions admin remove-cooldown [government]";
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        if(args.length==1)
            return TownyUtil.getGovernmentTabCompletions();

        return new ArrayList<>();
    }
}
