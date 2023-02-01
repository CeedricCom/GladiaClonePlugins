package com.ceedric.event.eventmobs;

import com.ceedric.event.eventmobs.config.MythicConfig;
import com.ceedric.event.eventmobs.config.TestMythicConfig;
import com.ceedric.event.eventmobs.config.YamlMythicConfig;
import com.ceedric.event.eventmobs.controller.Listeners;
import com.ceedric.event.eventmobs.controller.WorldService;
import com.ceedric.event.eventmobs.controller.command.EventCommand;
import com.ceedric.event.eventmobs.item.MartianStaff;
import com.ceedric.event.eventmobs.model.BossWorld;
import com.ceedric.event.eventmobs.model.MythicBoss;
import com.ceedric.event.eventmobs.model.participant.BossSide;
import com.ceedric.event.eventmobs.model.reward.ItemReward;
import com.ceedric.event.eventmobs.model.reward.TopNReward;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.MythicBukkit;
import me.deltaorion.bukkit.plugin.plugin.BukkitPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public final class EventMobs extends BukkitPlugin  {

    private BossWorld world;
    private WorldService service;
    private MythicConfig config;

    @Override
    public void onPluginEnable() {
        // Plugin startup logic
        this.config = createConfig();
        world = new BossWorld(config.getWorld());
        this.service = new WorldService(world);

        loadConfig();

        registerCommand(new EventCommand(this),"alienevent");
        getServer().getPluginManager().registerEvents(new Listeners(world,service),this);

        getCustomItemManager().registerItem(new MartianStaff());
    }

    private void loadConfig() {
        world.clearBosses();
        for(MythicBoss boss : config.getBosses()) {
            world.addBoss(boss);
        }

        for(Map.Entry<BossSide,String> name : config.getNames().entrySet()) {
            name.getKey().setFormattedName(name.getValue());
        }

        this.world.setWorld(config.getWorld());
    }

    public void reloadIConfig() {
        this.config.reload();
        loadConfig();
    }

    private MythicConfig createConfig() {
        /*
        World world = Bukkit.getWorld("world");
        MythicMob mob = MythicBukkit.inst().getMobManager().getMythicMob("Alien").orElse(null);
        MythicBoss boss = new MythicBoss(mob.getInternalName());
        TopNReward top1Reward = new TopNReward(1);
        top1Reward.addReward(new ItemReward("gaming",new ItemStack(Material.NAME_TAG),1));
        TopNReward top2Reward = new TopNReward(2);
        top2Reward.addReward(new ItemReward("nice",new ItemStack(Material.EMERALD),1));
        boss.addReward(top1Reward);
        boss.addReward(top2Reward);
        Map<BossSide,String> names = new HashMap<>();
        names.put(BossSide.BOSS,"Aliens");
        names.put(BossSide.PLAYERS,"Humans");

        TestMythicConfig config = new TestMythicConfig(new Location(world,64,world.getHighestBlockYAt(64,48),48),world,mob, names);
        config.addBoss(boss);


         */

        saveDefaultConfig();
        MythicConfig config = new YamlMythicConfig(this);
        config.reload();

        return config;
    }

    @Override
    public void onPluginDisable() {
        // Plugin shutdown logic
    }

    public BossWorld getWorld() {
        return world;
    }

    public WorldService getService() {
        return service;
    }

    public MythicConfig getIConfig() {
        return config;
    }

    public File getReportDir() {
        return new File(this.getDataFolder(),"reports");
    }
}
