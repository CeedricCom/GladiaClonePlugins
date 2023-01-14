package me.deltaorion.townymissionsv2.command.admin;

import me.deltaorion.townymissionsv2.TownyMissionsV2;
import me.deltaorion.townymissionsv2.command.ArgumentList;
import me.deltaorion.townymissionsv2.command.CommandException;
import me.deltaorion.townymissionsv2.command.CommandPermissions;
import me.deltaorion.townymissionsv2.command.SubCommand;
import me.deltaorion.townymissionsv2.mission.GoalGenerator;
import me.deltaorion.townymissionsv2.mission.MissionGenerator;
import me.deltaorion.townymissionsv2.mission.gather.GatherGoalGenerator;
import me.deltaorion.townymissionsv2.mission.reward.GoalReward;
import me.deltaorion.townymissionsv2.mission.reward.MissionReward;
import me.deltaorion.townymissionsv2.player.ContributeType;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DocumentationCommand implements SubCommand {
    @Override
    public void onCommand(TownyMissionsV2 plugin, CommandSender sender, ArgumentList args) throws CommandException {
        File file = plugin.getDataFolder().toPath().resolve("docs.txt").toFile();
        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            try(FileWriter writer = new FileWriter(file)) {
                writer.write("#### Town Missions" + System.lineSeparator());
                writeMissions(plugin.getPool().getGeneratorsOfType(ContributeType.TOWN),writer);
                writer.write(System.lineSeparator());

                writer.write("#### Nation Missions" + System.lineSeparator());
                writeMissions(plugin.getPool().getGeneratorsOfType(ContributeType.NATION),writer);
                writer.write(System.lineSeparator());
            }

            sender.sendMessage("Docs written to docs.txt");

        } catch (IOException e) {
            throw new CommandException(e.getMessage());
        }
    }

    private void writeMissions(List<MissionGenerator> generators, FileWriter writer) throws IOException {
        for(MissionGenerator generator : generators) {
            writer.write(generator.getName()+": "+System.lineSeparator());
            writer.write(System.lineSeparator());
            writer.write("| Goal | Rewards |" + System.lineSeparator());
            writer.write("| ----------------- | ----------------------- |" + System.lineSeparator());
            for(GoalGenerator goalGenerator : generator.getGoals()) {
                if(goalGenerator instanceof GatherGoalGenerator) {
                    GatherGoalGenerator g = (GatherGoalGenerator) goalGenerator;
                    StringBuilder rewardString = new StringBuilder();
                    int count = 0;
                    for(GoalReward reward : g.getRewards()) {
                        rewardString.append(String.format("%.2f", reward.getTotal())).append(" ").append(reward.getRewardName());
                        if(count<g.getRewards().size()-1) {
                            rewardString.append( ", ");
                        }
                        count++;
                    }
                    writer.write("| "+g.getGoal()+" "+toFriendly(g.getMaterial())+" | "+
                            rewardString+" |");

                    writer.write(System.lineSeparator());
                }
            }

            StringBuilder rewardString = new StringBuilder();
            int count = 0;
            for(MissionReward reward : generator.getRewards()) {
                rewardString.append(String.format("%.2f", reward.getTotal())).append(" ").append(reward.getRewardName());
                if(count<generator.getRewards().size()-1) {
                    rewardString.append(", ");
                }
                count++;
            }

            writer.write("| | "+rewardString+" |" + System.lineSeparator());
            writer.write(System.lineSeparator());
        }
    }

    private String toFriendly(Enum<?> zEnum) {
        String str = zEnum.toString();
        str = str.toLowerCase(Locale.ROOT);
        str = str.replace('_',' ');
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    @Override
    public String getDescription() {
        return "Creates docs";
    }

    @Override
    public String getPermission() {
        return CommandPermissions.OPERATOR.getPerm();
    }

    @Override
    public String getUsage() {
        return "get good";
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
