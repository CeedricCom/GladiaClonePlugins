package me.deltaorion.townymissionsv2.command.sub;

import com.google.common.collect.ImmutableList;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import me.deltaorion.townymissionsv2.Definition;
import me.deltaorion.townymissionsv2.TownyMissionsV2;
import me.deltaorion.townymissionsv2.bearer.MissionBearer;
import me.deltaorion.townymissionsv2.command.ArgumentList;
import me.deltaorion.townymissionsv2.command.CommandException;
import me.deltaorion.townymissionsv2.command.CommandPermissions;
import me.deltaorion.townymissionsv2.command.SubCommand;
import me.deltaorion.townymissionsv2.configuration.Message;
import me.deltaorion.townymissionsv2.display.MissionSound;
import me.deltaorion.townymissionsv2.mission.Mission;
import me.deltaorion.townymissionsv2.mission.chat.ChatDefinition;
import me.deltaorion.townymissionsv2.mission.gather.GatherGoal;
import me.deltaorion.townymissionsv2.mission.reward.GoalReward;
import me.deltaorion.townymissionsv2.mission.reward.MissionReward;
import me.deltaorion.townymissionsv2.mission.reward.type.*;
import me.deltaorion.townymissionsv2.player.ContributeType;
import me.deltaorion.townymissionsv2.player.MissionPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MissionDeveloperCommand implements SubCommand {
    @Override
    public void onCommand(TownyMissionsV2 plugin, CommandSender sender, ArgumentList args) throws CommandException {

        MissionPlayer player = null;

        if (sender instanceof Player) {
            player = plugin.getPlayerManager().getPlayer(sender);
        }

        switch (args.getArgOrFail(0).asString().toLowerCase()) {
            case "hasmission":
                if(player.getTown()!=null) {
                    sender.sendMessage("Mission: " + plugin.getMissionManager().hasMission(player));
                }
                break;

            case "printmissions":
                sender.sendMessage("Missions: "+plugin.getMissionManager().getMissions(player));
                break;

            case "givetest":
                if(player.getTown()!=null) {
                    handTest(player,plugin);
                }
                break;

            case "soundtest":
                player.getPlayer().sendMessage("Playing Sound... ");
                MissionSound.TEST.playSound(player);
                break;

            case "gathertest":
                if(player.getTown()!=null) {
                    gatherTest(player,plugin);
                }
                break;
            case "gatherdurtest":
                if(player.getTown()!=null) {
                    gatherDurTest(player,plugin);
                }
                break;

            case "cooldowntest":
                if(player.getTown()!=null) {
                    MissionBearer bearer = plugin.getMissionManager().getMissionBearer(player.getTown().getUUID());
                    bearer.setCooldown(Duration.of(5, ChronoUnit.SECONDS));
                    sender.sendMessage("Cool-down: "+bearer.onCooldown());
                }
                break;
            case "rewardtest":
                rewardTest(args,sender,plugin);
                break;

            case "savetest":
                plugin.getStorage().saveAll();
                break;

            default:
                player.getPlayer().sendMessage("Please enter a valid sub-command!");
        }
    }

    private void rewardTest(ArgumentList args, CommandSender sender,TownyMissionsV2 plugin) throws CommandException {
        args.setUsage("/missions dev exptest [player] [amount]");
        String playerName = args.getArgOrFail(1).asString();
        OfflinePlayer p = Bukkit.getOfflinePlayerIfCached(playerName);
        if(p==null)
            throw new CommandException(Message.COMMAND_NOT_PLAYER.getMessage());

        int amount = args.getArgOrFail(2).asInt();
        List<UUID> toGive = ImmutableList.of(p.getUniqueId());

        GoalReward exp = new GoalReward(amount,new ExperienceReward());
        GoalReward money = new GoalReward(amount,new MoneyReward());
        GoalReward item = new GoalReward(50,new ItemReward(new ItemStack(Material.DIAMOND,50)));
        GoalReward command = new GoalReward(1, new CommandReward("say %s cool! Amount Given %amount%","Boast"));


        Resident resident = TownyUniverse.getInstance().getResident(p.getUniqueId());
        Town town = resident.getTownOrNull();

        MissionReward reward = new MissionReward(1000,new GovernmentBankReward());

        exp.handRewardsEqually(toGive);
        money.handRewardsEqually(toGive);
        item.handRewardsEqually(toGive);
        command.handRewardsEqually(toGive);
        reward.handReward(plugin.getMissionManager().getMissionBearer(town.getUUID()));
        sender.sendMessage("Handed reward to offline player "+p.getName());
    }

    private void gatherDurTest(MissionPlayer player, TownyMissionsV2 plugin) {
        Mission mission = new Mission.Builder(plugin.getMissionManager().getMissionBearer(player.getTown().getUUID()),
                Duration.of(5,ChronoUnit.SECONDS))
                .appendGoal(new GatherGoal(
                        plugin.getDefinition(Definition.GATHER.getName()),
                        plugin.getMissionManager().getMissionBearer(player.getTown().getUUID()),
                        50,
                        Material.PUMPKIN
                ))
                .appendGoal(new GatherGoal(
                        plugin.getDefinition(Definition.GATHER.getName()),
                        plugin.getMissionManager().getMissionBearer(player.getTown().getUUID()),
                        100,
                        Material.MELON
                ))
                .build();

        plugin.getMissionManager().registerMission(mission);
    }

    private void gatherTest(MissionPlayer player, TownyMissionsV2 plugin) {
        Mission mission = new Mission.Builder(plugin.getMissionManager().getMissionBearer(player.getTown().getUUID()))
                .appendGoal(new GatherGoal(
                        plugin.getDefinition(Definition.GATHER.getName()),
                        plugin.getMissionManager().getMissionBearer(player.getTown().getUUID()),
                        50,
                        Material.PUMPKIN
                        ))
                .appendGoal(new GatherGoal(
                        plugin.getDefinition(Definition.GATHER.getName()),
                        plugin.getMissionManager().getMissionBearer(player.getTown().getUUID()),
                        100,
                        Material.MELON
                ))
                .build();

        plugin.getMissionManager().registerMission(mission);
    }

    private void handTest(MissionPlayer player, TownyMissionsV2 plugin) {

        Town town = player.getTown();
        MissionBearer bearer = plugin.getMissionManager().getMissionBearer(town.getUUID());
        ChatDefinition chatDefinition = (ChatDefinition) plugin.getDefinition("chat");
        Mission mission = new Mission.Builder(bearer)
                .appendGoal(chatDefinition.generateGoal(bearer,"Gamer",3,new GoalReward(5,new TestReward())))
                .appendGoal(chatDefinition.generateGoal(bearer,"help",3))
                .appendGoal(chatDefinition.generateGoal(bearer,"world",2, new GoalReward(5000, new ExperienceReward())))
                .build();

        player.getPlayer().sendMessage(mission.toString());
        player.getPlayer().sendMessage(plugin.getMissionManager().getMissionBearer(town.getUUID()).toString());


        plugin.getMissionManager().registerMission(mission);
        player.getPlayer().sendMessage("Registered Mission");
    }

    @Override
    public String getDescription() {
        return "Commands used to Test and Develop the Plugin";
    }

    @Override
    public String getPermission() {
        return CommandPermissions.OPERATOR.getPerm();
    }

    @Override
    public String getUsage() {
        return "Look at tab completions for help";
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();
        completions.add("hasmission");
        completions.add("printmissions");
        completions.add("givetest");
        completions.add("gathertest");
        completions.add("soundtest");
        completions.add("cooldowntest");
        completions.add("gatherdurtest");
        completions.add("rewardtest");
        completions.add("savetest");
        return completions;
    }
}
