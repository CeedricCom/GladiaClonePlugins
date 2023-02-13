package me.deltaorion.townymissionsv2.mission.gather;

import com.google.common.base.Preconditions;
import me.deltaorion.townymissionsv2.Definition;
import me.deltaorion.townymissionsv2.TownyMissionsV2;
import me.deltaorion.townymissionsv2.bearer.MissionBearer;
import me.deltaorion.townymissionsv2.configuration.Message;
import me.deltaorion.townymissionsv2.mission.Mission;
import me.deltaorion.townymissionsv2.mission.definition.GoalDefinition;
import me.deltaorion.townymissionsv2.mission.goal.CollectiveGoal;
import me.deltaorion.townymissionsv2.mission.goal.ContributableGoal;
import me.deltaorion.townymissionsv2.mission.reward.GoalReward;
import me.deltaorion.townymissionsv2.player.MissionPlayer;
import me.deltaorion.townymissionsv2.util.DurationParser;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GatherGoal extends CollectiveGoal implements ContributableGoal {

    private final Material toGather;
    private final MissionBearer master;

    public GatherGoal(GoalDefinition definition, MissionBearer master, int goal, Material toGather) {
        super(goal,master, definition);
        this.toGather = toGather;
        this.master = master;
    }

    public GatherGoal(GoalDefinition definition, MissionBearer master, int goal, Material toGather, List<GoalReward> rewardList) {
        super(goal,master,definition,rewardList);
        this.toGather = toGather;
        this.master = master;
    }

    protected GatherGoal(GoalDefinition definition, MissionBearer master, Mission mission , int goal, int progress, Map<UUID,Integer> contributions, Material material, List<GoalReward> rewards) {
        super(master,mission,definition,progress,goal,contributions, rewards);
        this.toGather = material;
        this.master = master;
    }

    public static GatherGoal fromSave(TownyMissionsV2 plugin, Definition definition, MissionBearer master, Mission mission , int goal, int progress, Map<UUID,Integer> contributions, String materialString, List<GoalReward> rewards) {
        Preconditions.checkNotNull(definition);
        Preconditions.checkNotNull(contributions);
        Preconditions.checkNotNull(definition);

        Material material = null;
        try {
            material = Material.matchMaterial(materialString);
        } catch (IllegalArgumentException e) {
            Bukkit.getLogger().severe("Could not load goal for mission '"+mission.getUniqueID()+"' as the material '"+materialString+"' is not a valid material!");
            return null;
        }

        GoalDefinition goalDefinition = plugin.getDefinition(definition);

        return new GatherGoal(goalDefinition,master,mission,goal,progress,contributions,material,rewards);
    }

    @Override
    public String getName() {
        return "Gather";
    }

    @Override
    public String getDisplayText() {
        return Message.GOV_GATHER_TEXT.getMessage(getGoal(),toGather.toString().toLowerCase(),getProgress());
    }

    public String toString() {
        return  "For: " +getMaster().getUniqueID().toString() + " Goal: "+getGoal() + " Progress: "+getProgress();
    }

    public Material getToGather() {
        return toGather;
    }

    @Override
    public void contribute(MissionPlayer player) {

        Preconditions.checkNotNull(player);

        if(isComplete())
            return;

        long contributeLock = player.getContributionLockTime(master);
        if(contributeLock > 0) {
            player.getPlayer().sendMessage(Message.CONTRIBUTION_LOCK_ERROR.getMessage(DurationParser.print(Duration.ofMillis(contributeLock))));
            return;
        }

        player.setContributionLock(master);

        int required = getGoal()-getProgress();
        int contributed = 0;
        for(int i=0;i<player.getPlayer().getInventory().getSize();i++) {
            ItemStack itemStack = player.getPlayer().getInventory().getItem(i);

            if(itemStack==null)
                continue;

            ItemAlteration alteration = contributeItem(player,itemStack, required);

            if(alteration.destroy) {
                destroyItem(player,i);
            }

            required = required-alteration.contributed;
            contributed += alteration.contributed;

            if(required==0)
                break;
        }

        contribute(player.getUuid(),contributed);
    }

    private void destroyItem(MissionPlayer player, int slot) {
        player.getPlayer().getInventory().setItem(slot,null);
    }

    private ItemAlteration contributeItem(MissionPlayer player, ItemStack itemStack, int required) {

        Preconditions.checkNotNull(itemStack);

        boolean destroy = false;
        int contributed = 0;

        if(itemStack.getType().equals(toGather)) {
            if(getAmount(player,itemStack) <= required) {
                contributed = getAmount(player,itemStack);
                destroy = true;
            } else {
                itemStack.setAmount((int) (itemStack.getAmount() - (required/player.getContributionMultiplier())));
                contributed = required;
            }
        }
        return new ItemAlteration(destroy, contributed);
    }

    private int getAmount(MissionPlayer player, ItemStack itemStack) {
        return (int) (itemStack.getAmount() * player.getContributionMultiplier());
    }


    public boolean contribute(MissionPlayer player, Item item) {
        if(isComplete())
            return false;

        ItemStack itemStack = item.getItemStack();
        int required = getGoal()-getProgress();

        ItemAlteration alteration = contributeItem(player,itemStack,required);
        contribute(player.getUuid(),alteration.contributed);

        item.setItemStack(itemStack);

        return alteration.destroy;
    }

    //INSERT INTO MissionGoal VALUES('Mission',Stage,Goal,definition,progress)
    //INSERT INTO GatherGoal VALUES(Stage,Mission,Material);

    public void loadParameters(PreparedStatement sup, PreparedStatement sub,PreparedStatement contributions, int stage) throws SQLException {
        loadParameters(sup,stage);
        saveContributions(contributions,stage);

        sub.setInt(1,stage);
        sub.setString(2,getMission().getUniqueID().toString());
        sub.setString(3,toGather.toString());

        sub.addBatch();
    }

    public static class ItemAlteration {
        private final boolean destroy;
        private final int contributed;

        public ItemAlteration(boolean destroy, int contributed) {
            this.destroy = destroy;
            this.contributed = contributed;
        }
    }

}
