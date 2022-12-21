package me.deltaorion.consumescrolls;

import me.deltaorion.bukkit.item.EMaterial;
import me.deltaorion.bukkit.plugin.plugin.BukkitPlugin;
import me.deltaorion.consumescrolls.command.ConsumeScrollCommand;
import me.deltaorion.consumescrolls.config.TestScrollConfig;
import me.deltaorion.consumescrolls.reward.CommandReward;
import me.deltaorion.consumescrolls.reward.ItemReward;
import me.deltaorion.consumescrolls.reward.ScrollReward;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class ConsumeScrollPlugin extends BukkitPlugin {

    private ScrollPool pool;
    private ConsumeScrollGenerator generator;

    /**
     * TODO
     *   - Shape Command
     *     ScrollAdmin
     *       - reload
     *       - give [name/rarity]
     *   - configuration
     */

    @Override
    public void onPluginEnable() {
        pool = new ScrollPool(this);

        List<String> toolTip = new ArrayList<>();
        toolTip.add("tool");
        toolTip.add("tip");

        List<ScrollDefinition> scrolls = new ArrayList<>();
        ScrollDefinition scr = new ScrollDefinition("gaming",EMaterial.PUMPKIN,Rarity.COMMON,5,64);
        scr.addReward(new CommandReward("experience add {0} 500 levels"));
        scr.addReward(new ItemReward(new ItemStack(Material.NAME_TAG,64)));


        TestScrollConfig config = new TestScrollConfig(toolTip,true,"Congratulations",scrolls);
        scrolls.add(scr);

        for(ScrollDefinition definition : scrolls) {
            pool.addScroll(definition);
        }

        generator = new ConsumeScrollGenerator(config,pool);
        getCustomItemManager().registerItem(generator);

        registerCommand(new ConsumeScrollCommand(pool,generator),"scrolladmin");
    }

    public ScrollPool getPool() {
        return pool;
    }

    public ConsumeScrollGenerator getGenerator() {
        return generator;
    }
}
