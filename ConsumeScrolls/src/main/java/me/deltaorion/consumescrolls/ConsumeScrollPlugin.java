package me.deltaorion.consumescrolls;

import me.deltaorion.bukkit.plugin.plugin.BukkitPlugin;
import me.deltaorion.consumescrolls.command.ConsumeScrollCommand;
import me.deltaorion.consumescrolls.config.ConfigurationException;
import me.deltaorion.consumescrolls.config.ScrollConfig;
import me.deltaorion.consumescrolls.config.TestScrollConfig;
import me.deltaorion.consumescrolls.config.YamlScrollConfig;
import me.deltaorion.consumescrolls.reward.CommandReward;
import me.deltaorion.consumescrolls.reward.ItemReward;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ConsumeScrollPlugin extends BukkitPlugin {

    private ScrollPool pool;
    private ConsumeScrollGenerator generator;
    private ScrollConfig config;

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

        try {
            createConfig();
        } catch (ConfigurationException e) {
            Bukkit.getLogger().warning("Invalid Config: "+e.getMessage());
            disablePlugin();
            return;
        }
        for(ScrollDefinition definition : config.getScrolls()) {
            pool.addScroll(definition);
        }

        generator = new ConsumeScrollGenerator(config,pool);
        getCustomItemManager().registerItem(generator);

        registerCommand(new ConsumeScrollCommand(this),"scrolladmin");
    }

    private void createConfig() throws ConfigurationException {
        //config = getTest1();
        config = createYamlConfig();
    }

    public ScrollConfig getConfiguration() {
        return config;
    }

    private ScrollConfig createYamlConfig() throws ConfigurationException {
        return new YamlScrollConfig(new File(getDataFolder(),"config.yml"),this);
    }

    public ScrollPool getPool() {
        return pool;
    }

    public ConsumeScrollGenerator getGenerator() {
        return generator;
    }

    private ScrollConfig getTest1() {
        List<String> toolTip = new ArrayList<>();
        toolTip.add("tool");
        toolTip.add("tip");

        List<ScrollDefinition> scrolls = new ArrayList<>();
        ScrollDefinition scr = new ScrollDefinition("gaming",Material.PUMPKIN,Rarity.COMMON,5,64);
        scr.addReward(new CommandReward("experience add {0} 500 levels"));
        scr.addReward(new ItemReward(new ItemStack(Material.NAME_TAG),64));

        Map<Rarity,Integer> percentages = new HashMap<>();


        TestScrollConfig config = new TestScrollConfig(toolTip,true,"Congratulations",scrolls, percentages);
        scrolls.add(scr);

        return config;
    }

}
