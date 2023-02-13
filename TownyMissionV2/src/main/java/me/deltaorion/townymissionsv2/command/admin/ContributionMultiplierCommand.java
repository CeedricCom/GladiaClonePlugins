package me.deltaorion.townymissionsv2.command.admin;

import me.deltaorion.townymissionsv2.TownyMissionsV2;
import me.deltaorion.townymissionsv2.command.ArgumentList;
import me.deltaorion.townymissionsv2.command.CommandException;
import me.deltaorion.townymissionsv2.command.CommandPermissions;
import me.deltaorion.townymissionsv2.command.SubCommand;
import me.deltaorion.townymissionsv2.player.MissionPlayer;
import me.deltaorion.townymissionsv2.util.DurationParser;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class ContributionMultiplierCommand implements SubCommand {
    @Override
    public void onCommand(TownyMissionsV2 plugin, CommandSender sender, ArgumentList args) throws CommandException {
        double multiplier = args.getArgOrFail(0).asDouble();
        Player player = args.getArgOrFail(1).asPlayer();
        Duration time = args.getArgOrFail(2).asDuration();

        MissionPlayer missionPlayer = TownyMissionsV2.getInstance().getPlayerManager().getPlayer(player);
        if(missionPlayer==null)
            throw new CommandException("Unknown Player");

        missionPlayer.setTemporaryContributionMultiplier(multiplier,time);

        sender.sendMessage("Set "+multiplier+"x contribution multiplier for '"+player.getName()+"' for "+ DurationParser.print(time));
    }

    @Override
    public String getDescription() {
        return "Sets the users contribution multiplier";
    }

    @Override
    public String getPermission() {
        return CommandPermissions.ADMIN.getPerm();
    }

    @Override
    public String getUsage() {
        return "/missions admin multiplier [multiplier] [player] [time]";
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
