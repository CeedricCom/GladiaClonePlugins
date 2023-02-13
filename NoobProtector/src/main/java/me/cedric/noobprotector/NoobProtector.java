package me.cedric.noobprotector;

import me.cedric.noobprotector.command.PvPAdmin;
import me.cedric.noobprotector.command.PvPCommand;
import me.cedric.noobprotector.listener.PlayerListener;
import me.cedric.noobprotector.util.NPUtil;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class NoobProtector extends JavaPlugin {

    public boolean joinprotect = true;
    public boolean userealtime = true;
    public boolean useplaytime = true;
    public int prttime = 2880;
    public int prtplay = 300;
    public int pvponcooldown = 10;
    public int pvpupdatetime = 5;
    public boolean playerwarn = true;
    public int playerwarntime = 30;
    public String timezone = "";
    public List<String> no_pvp_worlds = new ArrayList<>();
    public NPUtil u;
    public NPPList players;
    public static NoobProtector i;

    public void onEnable() {
        i = this;
        if (!this.getDataFolder().exists()) {
            this.getDataFolder().mkdirs();
        }
        Messages.initialize();
        this.loadCfg();
        this.saveCfg();
        this.u = new NPUtil(this, "pvpadmin", "NoobProtector");
        this.players = new NPPList(this);
        this.getCommand("pvpadmin").setExecutor(new PvPAdmin(this));
        this.getCommand("pvp-on").setExecutor(new PvPCommand());
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
    }

    public void onDisable() {
        this.players.savePlayerList();
    }

    public void saveCfg() {
        this.getConfig().set("general.time-zone", this.timezone);
        this.getConfig().set("protect.after-join", this.joinprotect);
        this.getConfig().set("protect.realtime.enable", this.userealtime);
        this.getConfig().set("protect.realtime.time", this.prttime);
        this.getConfig().set("protect.playtime.enable", this.useplaytime);
        this.getConfig().set("protect.playtime.time", this.prtplay);
        this.getConfig().set("general.pvp-on-cool-down", this.pvponcooldown);
        this.getConfig().set("schedule.pvp-update-time", this.pvpupdatetime);
        this.getConfig().set("schedule.player-warn.enable", this.playerwarn);
        this.getConfig().set("schedule.player-warn.time", this.playerwarntime);
        this.getConfig().set("unprotected.worlds", this.no_pvp_worlds);
        this.saveConfig();
    }

    public void loadCfg() {
        this.joinprotect = this.getConfig().getBoolean("protect.after-join");
        this.userealtime = this.getConfig().getBoolean("protect.realtime.enable");
        this.prttime = this.getConfig().getInt("protect.realtime.time");
        this.useplaytime = this.getConfig().getBoolean("protect.playtime.enable");
        this.prtplay = this.getConfig().getInt("protect.playtime.time");
        if (!this.userealtime && !this.useplaytime) {
            this.useplaytime = true;
        }
        this.timezone = this.getConfig().getString("general.time-zone");
        this.pvponcooldown = this.getConfig().getInt("general.pvp-on-cool-down");
        this.pvpupdatetime = this.getConfig().getInt("schedule.pvp-update-time");
        this.playerwarn = this.getConfig().getBoolean("schedule.player-warn.enable");
        this.playerwarntime = this.getConfig().getInt("schedule.player-warn.time");
        this.no_pvp_worlds = this.getConfig().getStringList("unprotected.worlds");
    }
}
