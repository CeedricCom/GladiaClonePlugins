package me.deltaorion.townymissionsv2.command.sub;

import me.deltaorion.townymissionsv2.TownyMissionsV2;
import me.deltaorion.townymissionsv2.command.ArgumentList;
import me.deltaorion.townymissionsv2.command.CommandException;
import me.deltaorion.townymissionsv2.command.CommandPermissions;
import me.deltaorion.townymissionsv2.command.SubCommand;
import me.deltaorion.townymissionsv2.configuration.Message;
import me.deltaorion.townymissionsv2.mission.Mission;
import me.deltaorion.townymissionsv2.mission.goal.ContributableGoal;
import me.deltaorion.townymissionsv2.player.ContributeType;
import me.deltaorion.townymissionsv2.player.MissionPlayer;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class ContributeCommand implements SubCommand {
    @Override
    public void onCommand(TownyMissionsV2 plugin, CommandSender sender, ArgumentList args) throws CommandException {
        MissionPlayer player = plugin.getPlayerManager().getPlayer(sender);
        ContributeType type = args.getArgOrNothing(0).asEnumOrDefault(ContributeType.class, player.getContributePriority());

        Mission mission = plugin.getMissionManager().getPrimaryMission(player, type);
        if (mission == null)
            throw new CommandException(Message.COMMAND_NO_MISSION.getMessage());

        if (mission.missionOver())
            throw new CommandException(Message.COMMAND_NO_MISSION.getMessage());

        if (mission.getCurrentGoal() == null)
            throw new CommandException(Message.ERROR_ACTIVE_NO_GOAL.getMessage());

        if (!(mission.getCurrentGoal() instanceof ContributableGoal))
            throw new CommandException(Message.COMMAND_NOT_PRIMARY.getMessage(type.toString().toLowerCase()));

        ContributableGoal goal = (ContributableGoal) mission.getCurrentGoal();
        goal.contribute(player);
    }

    @Override
    public String getDescription() {
        return "Contributes items to the primary mission. Note this will REMOVE all required items!";
    }

    @Override
    public String getPermission() {
        return CommandPermissions.GENERIC.getPerm();
    }

    @Override
    public String getUsage() {
        return "/missions contribute <town/nation>";
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        for (ContributeType contributeType : ContributeType.values()) {
            list.add(contributeType.toString().toLowerCase());
        }

        return list;
    }
}
