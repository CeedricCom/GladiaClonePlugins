package me.deltaorion.townymissionsv2.test;

import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Town;
import me.deltaorion.townymissionsv2.TownyMissionsV2;
import me.deltaorion.townymissionsv2.bearer.MissionBearer;
import me.deltaorion.townymissionsv2.configuration.Message;
import me.deltaorion.townymissionsv2.mission.Mission;
import me.deltaorion.townymissionsv2.mission.MissionGoal;
import me.deltaorion.townymissionsv2.mission.chat.ChatDefinition;
import me.deltaorion.townymissionsv2.mission.gather.GatherGoal;
import me.deltaorion.townymissionsv2.mission.reward.GoalReward;
import me.deltaorion.townymissionsv2.mission.reward.type.ExperienceReward;
import me.deltaorion.townymissionsv2.mission.reward.type.TestReward;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class MissionTest implements CommandExecutor {

    private final TownyMissionsV2 plugin;

    public MissionTest(TownyMissionsV2 plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(!sender.isOp())
            sender.sendMessage(Message.NO_PERMISSION.getMessage());

        if(sender instanceof Player) {
            //place test logic here
            Player player = (Player) sender;
            Town town = null;
            Nation nation = null;
            try {
                town = TownyUniverse.getInstance().getResident(player.getUniqueId()).getTown();
                if(town.hasNation()) {
                    nation = town.getNation();
                }

                chatTest(sender,town);
            } catch (NotRegisteredException e) {
                e.printStackTrace();
            }

            if(town==null)
                sender.sendMessage("get a town noob");

        } else {
            sender.sendMessage("Test as a player LOL");
        }
        return true;
    }

    public void testGeneration(CommandSender sender, Town town) {
    }

    public void testGoalInterface(Player player, Town town) {
        List<GoalReward> rewardList = new ArrayList<>();
        rewardList.add(new GoalReward(50,new TestReward()));
        rewardList.add(new GoalReward(1000,new ExperienceReward()));

        MissionBearer bearer = plugin.getMissionManager().getMissionBearer(town.getUUID());

        MissionGoal missionGoal = new GatherGoal(plugin.getDefinition("gather"),bearer,50,Material.PUMPKIN,rewardList);
        missionGoal.contribute(player.getUniqueId(),5);
        missionGoal.contribute(player.getUniqueId(),5);
        missionGoal.contribute(player.getUniqueId(),5);
        int leftOver = missionGoal.contribute(player.getUniqueId(),50);

        System.out.println(leftOver);
    }

    public void testReward(CommandSender sender, Town town) {
        GoalReward goalReward = new GoalReward(50,new TestReward());
        Map<UUID,Integer> contributions = new HashMap<>();
        contributions.put(new UUID(0,0),10);
        contributions.put(new UUID(0,1),20);
        contributions.put(new UUID(0,2),10);
        goalReward.handRewardsOnContribution(contributions);
    }

    public void chatTest(CommandSender sender, Town town) {
        ChatDefinition chatDefinition = (ChatDefinition) plugin.getDefinition("chat");
        MissionBearer bearer = plugin.getMissionManager().getMissionBearer(town.getUUID());
        Mission mission = new Mission.Builder(bearer)
                .appendGoal(chatDefinition.generateGoal(bearer,"Gamer",3,new GoalReward(5,new TestReward())))
                .appendGoal(chatDefinition.generateGoal(bearer,"help",3))
                .appendGoal(chatDefinition.generateGoal(bearer,"world",2, new GoalReward(5000, new ExperienceReward())))
                .build();

        System.out.println("UUID: "+mission.getUniqueID());
        System.out.println("Mission Over: "+mission.missionOver());
        System.out.println("Expired: "+mission.missionExpired());
        System.out.println("Goal: "+mission.getCurrentGoal());
        System.out.println("Duration: "+mission.getDuration());
        System.out.println("Stage: "+mission.getStage());


        plugin.getMissionManager().registerMission(mission);
        sender.sendMessage("Registered Mission");
    }
}
