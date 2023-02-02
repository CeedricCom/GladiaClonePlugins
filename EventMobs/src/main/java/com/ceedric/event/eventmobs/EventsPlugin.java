package com.ceedric.event.eventmobs;

import com.ceedric.event.eventmobs.config.EventConfig;
import com.ceedric.event.eventmobs.config.YamlEventConfig;
import com.ceedric.event.eventmobs.controller.Listeners;
import com.ceedric.event.eventmobs.controller.command.admin.AdminCommand;
import com.ceedric.event.eventmobs.controller.command.player.PlayerCommand;
import com.ceedric.event.eventmobs.item.MartianStaff;
import com.ceedric.event.eventmobs.model.boss.BossEvent;
import com.ceedric.event.eventmobs.model.Event;
import com.ceedric.event.eventmobs.model.EventService;
import com.ceedric.event.eventmobs.model.boss.BossSideEnum;
import me.deltaorion.bukkit.plugin.plugin.BukkitPlugin;

import java.io.File;
import java.util.Map;

public final class EventsPlugin extends BukkitPlugin  {

    private EventService service;
    private EventConfig config;
    private PlayerCommand command;

    @Override
    public void onPluginEnable() {
        // Plugin startup logic
        this.config = createConfig();
        this.service = new EventService();
        this.command = new PlayerCommand(this);

        loadConfig();

        registerCommand(new AdminCommand(this),"eventadmin");
        registerCommand(command,"event");


        getServer().getPluginManager().registerEvents(new Listeners(service),this);

        getCustomItemManager().registerItem(new MartianStaff());
    }

    private void loadConfig() {
        service.clearEvents();
        command.deregister();

        for(Event event : config.getEvents()) {
            service.addEvent(event);
            command.register(event);
        }

        for(Map.Entry<BossSideEnum,String> name : config.getNames().entrySet()) {
            name.getKey().setFormattedName(name.getValue());
        }
    }

    public void reloadIConfig() {
        this.config.reload();
        loadConfig();
    }

    private EventConfig createConfig() {
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
        EventConfig config = new YamlEventConfig(this);
        config.reload();

        return config;
    }

    @Override
    public void onPluginDisable() {
        // Plugin shutdown logic
    }

    public EventService getService() {
        return service;
    }

    public EventConfig getIConfig() {
        return config;
    }

    public File getReportDir() {
        return new File(this.getDataFolder(),"reports");
    }
}
