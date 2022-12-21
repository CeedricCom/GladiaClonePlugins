package me.deltaorion.townymissionsv2.command.admin;

import com.palmergames.bukkit.towny.object.Government;
import me.deltaorion.townymissionsv2.TownyMissionsV2;
import me.deltaorion.townymissionsv2.bearer.MissionBearer;
import me.deltaorion.townymissionsv2.command.ArgumentList;
import me.deltaorion.townymissionsv2.command.CommandException;
import me.deltaorion.townymissionsv2.command.CommandPermissions;
import me.deltaorion.townymissionsv2.command.sub.MissionAdminCommand;
import me.deltaorion.townymissionsv2.mission.MissionGenerator;
import me.deltaorion.townymissionsv2.util.TownyUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class MissionAdminGiveCommand implements me.deltaorion.townymissionsv2.command.SubCommand {

    private final TownyMissionsV2 plugin;

    public MissionAdminGiveCommand(TownyMissionsV2 plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onCommand(TownyMissionsV2 plugin, CommandSender sender, ArgumentList args) throws CommandException {
        Government government = args.getArgOrFail(0).parse(Government.class,MissionAdminCommand.GOV_PARSER);
        MissionBearer bearer = plugin.getMissionManager().getMissionBearer(government.getUUID());

        MissionGenerator generator = plugin.getPool().getGenerator(args.getArgOrFail(1).asString());
        if(generator==null)
            throw new CommandException(ChatColor.RED+"Could not find command generator!");

        int amount = args.getArgOrNothing(2).asIntOrDefault(1);
        for(int i=0;i<amount;i++) {
            bearer.addMission(generator.getMission(bearer));
        }

        sender.sendMessage("Successfully Added Mission: "+args.getArgOrFail(1).asString());
    }

    @Override
    public String getDescription() {
        return "gives the selected town a mission. This will give the mission no matter what even if it normally should not be given to said type";
    }

    @Override
    public String getPermission() {
        return CommandPermissions.ADMIN_GIVE_MISSION.getPerm();
    }

    @Override
    public String getUsage() {
        return "/missions admin give-mission [government] [mission-name] <primary>";
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        if(args.length==1) {
            return TownyUtil.getGovernmentTabCompletions();
        } else if(args.length==2) {
            List<String> missions = new ArrayList<>();
            for(MissionGenerator generator : plugin.getPool().getPool())
                missions.add(generator.getName());

            return missions;
        }

        return new ArrayList<>();
    }
}
