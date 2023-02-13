package me.cedric.noobprotector.util;

import me.cedric.noobprotector.Messages;
import me.cedric.noobprotector.NoobProtector;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class NPUtil extends FGUtilCore {

    NoobProtector plg;

    public NPUtil(NoobProtector plugin, String plgcmd, String px) {
        super(plugin, plgcmd, px);
        this.plg = plugin;
        this.InitCmd();
    }

    public void PrintCfg(Player p) {
        this.printMsg(p, "&6&lNoobProtector " + " &r&6| " + Messages.getMSG("cfg_configuration", '6'));
        this.printMSG(p, "msg_currenttime", this.getServerTime(""));
        this.printMSG(p, "msg_servertimezone", TimeZone.getDefault().getDisplayName());
        this.printMSG(p, "msg_cfgtimezone", this.plg.timezone);
        this.printMSG(p, "msg_currenttime", this.getServerTime(this.plg.timezone));
        this.printEnDis(p, "msg_joinprotect", this.plg.joinprotect);
        this.printEnDis(p, "msg_playerwarn", this.plg.playerwarn);
        this.printMSG(p, "msg_playerwarntime", this.plg.playerwarntime);
        this.printEnDis(p, "msg_useplaytime", this.plg.useplaytime);
        this.printMSG(p, "msg_useplaytimevalue", this.plg.prtplay);
        this.printEnDis(p, "msg_userealtime", this.plg.userealtime);
        this.printMSG(p, "msg_userealtimevalue", this.plg.prttime);
        this.printMSG(p, "msg_pvponcooldowntime", this.plg.pvponcooldown);
        this.printMSG(p, "msg_pvpupdatetime", this.plg.pvpupdatetime);
    }

    public void InitCmd() {
        this.cmds.clear();
        this.cmdlist = "";
        this.addCmd("help", "config", "hlp_thishelp", "&3/pvpadmin help [command]", true);
        this.addCmd("protect", "protect", "cmd_protect", "&3/pvpadmin protect [player]", true);
        this.addCmd("unprotect", "unprotect", "cmd_unprotect", "&3/pvpadmin unprotect [player]", true);
        this.addCmd("list", "config", "cmd_list", "&3/pvpadmin list [page] [name mask]", true);
        this.addCmd("reload", "config", "cmd_reload", "&a/pvpadmin reload", true);
        this.addCmd("cfg", "config", "cmd_cfg", "&3/pvpadmin cfg", true);
    }

    public String getServerTime(String tzone) {
        final Date d = new Date(System.currentTimeMillis());
        final SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        if (!tzone.isEmpty()) {
            f.setTimeZone(TimeZone.getTimeZone(tzone));
        }
        return f.format(d);
    }

    public Player getPlayerByName(String playerName) {
        for (final Player player : Bukkit.getOnlinePlayers()) {
            if (player.getName().equalsIgnoreCase(playerName)) {
                return player;
            }
        }
        return null;
    }


}
