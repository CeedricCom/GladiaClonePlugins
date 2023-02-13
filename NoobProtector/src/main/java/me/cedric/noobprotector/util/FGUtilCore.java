package me.cedric.noobprotector.util;

import java.util.ArrayList;

import me.cedric.noobprotector.Messages;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import java.util.Random;
import java.util.HashMap;
import org.bukkit.plugin.java.JavaPlugin;

public class FGUtilCore {

    public JavaPlugin plg;
    public String px;
    private final String permprefix;
    private String plgcmd;
    protected String msglist;
    protected HashMap<String, Cmd> cmds;
    protected String cmdlist;
    public Random random;

    public FGUtilCore(final JavaPlugin plg, final String plgcmd, final String px) {
        this.px = "";
        this.permprefix = "fgutilcore.";
        this.plgcmd = "<command>";
        this.msglist = "";
        this.cmds = new HashMap<>();
        this.cmdlist = "";
        this.random = new Random();
        this.plg = plg;
        this.px = px;
        this.plgcmd = plgcmd;
    }

    public void addCmd(final String cmd, final String perm, final String desc_id, final String desc_key, final boolean console) {
        final String desc = Messages.getMSG(desc_id, desc_key);
        this.cmds.put(cmd, new Cmd(this.permprefix + perm, desc, console));
        if (this.cmdlist.isEmpty()) {
            this.cmdlist = cmd;
        }
        else {
            this.cmdlist = this.cmdlist + ", " + cmd;
        }
    }

    public boolean checkCmdPerm(Player p, final String cmd) {

        if (!this.cmds.containsKey(cmd.toLowerCase())) {
            return false;
        }

        final Cmd cm = this.cmds.get(cmd.toLowerCase());
        return p.hasPermission(cm.perm);
    }

    public void printPage(Player p, final List<String> ln, final int pnum, final String title, final String footer, final boolean shownum) {
        final PageList pl = new PageList(ln, title, footer, shownum);
        pl.printPage(p, pnum);
    }

    public void printPage(Player p, final List<String> ln, final int pnum, final String title, final String footer,
                          final boolean shownum, final int lineperpage) {
        final PageList pl = new PageList(ln, title, footer, shownum);
        pl.printPage(p, pnum, lineperpage);
    }

    public void printMsg(Player p, final String msg) {
        String message = ChatColor.translateAlternateColorCodes('&', msg);
        p.sendMessage(message);
    }

    public void printMSG(Player p, final Object... s) {
        String message = Messages.getMSG(s);
        p.sendMessage(message);
    }

    public void PrintHlpList(Player p, final int page, final int lpp) {
        final String title = Messages.getMSG("hlp_help", '6');
        final List<String> hlp = new ArrayList<>();
        hlp.add(Messages.getMSG("hlp_thishelp", "/" + this.plgcmd + " help"));
        hlp.add(Messages.getMSG("hlp_execcmd", "/" + this.plgcmd + " <" + Messages.getMSG("hlp_cmdparam_command") + "> [" + Messages.getMSG("hlp_cmdparam_parameter") + "]"));
        hlp.add(Messages.getMSG("hlp_typecmdpage", "/" + this.plgcmd + " help <" + Messages.getMSG("hlp_cmdparam_page") + ">"));
        final String[] ks = this.cmdlist.replace(" ", "").split(",");
        if (ks.length > 0) {
            for (final String cmd : ks) {
                hlp.add(this.cmds.get(cmd).desc);
            }
        }
        this.printPage(p, hlp, page, title, "", false, lpp);
    }

    public String EnDis(final boolean b) {
        return b ? Messages.getMSG("enabled", '2') : Messages.getMSG("disabled", 'c');
    }

    public void printEnDis(Player p, final String msg_id, final boolean b) {
        p.sendMessage(Messages.getMSG(msg_id) + ": " + this.EnDis(b));
    }

    public String getMSGnc(final Object... s) {
        return ChatColor.stripColor(Messages.getMSG(s));
    }

    public boolean isInteger(final String str) {
        return str.matches("[0-9]+[0-9]*");
    }

    public class Cmd {
        String perm;
        String desc;
        boolean console;

        public Cmd(final String perm, final String desc, final boolean console) {
            this.perm = perm;
            this.desc = desc;
            this.console = console;
        }
    }

    public class PageList {
        private final List<String> ln;
        private final int lpp;
        private String title_msgid;
        private String footer_msgid;
        private boolean shownum;

        public PageList(final List<String> ln, final String title_msgid, final String footer_msgid, final boolean shownum) {
            this.lpp = 15;
            this.title_msgid = "lst_title";
            this.footer_msgid = "lst_footer";
            this.shownum = false;
            this.ln = ln;
            if (!title_msgid.isEmpty()) {
                this.title_msgid = title_msgid;
            }
            if (!footer_msgid.isEmpty()) {
                this.footer_msgid = footer_msgid;
            }
            this.shownum = shownum;
        }

        public void printPage(Player p, final int pnum) {
            this.printPage(p, pnum, this.lpp);
        }

        public void printPage(Player p, int pnum, final int linesperpage) {
            if (this.ln.size() > 0) {
                int maxp = this.ln.size() / linesperpage;
                if (this.ln.size() % linesperpage > 0) {
                    ++maxp;
                }
                if (pnum > maxp) {
                    pnum = maxp;
                }
                int maxl = linesperpage;
                if (pnum == maxp) {
                    maxl = this.ln.size() % linesperpage;
                    if (maxp == 1) {
                        maxl = this.ln.size();
                    }
                }
                if (maxl == 0) {
                    maxl = linesperpage;
                }
                if (Messages.isMessage(this.title_msgid)) {
                    FGUtilCore.this.printMsg(p, "&6&l" + FGUtilCore.this.getMSGnc(this.title_msgid));
                }
                else {
                    FGUtilCore.this.printMsg(p, this.title_msgid);
                }
                String numpx = "";
                for (int i = 0; i < maxl; ++i) {
                    if (this.shownum) {
                        numpx = "&3" + (1 + i + (pnum - 1) * linesperpage) + ". ";
                    }
                    FGUtilCore.this.printMsg(p, numpx + "&a" + this.ln.get(i + (pnum - 1) * linesperpage));
                }
                if (maxp > 1) {
                    FGUtilCore.this.printMSG(p, this.footer_msgid, 'e', pnum, maxp);
                }
            }
            else {
                FGUtilCore.this.printMSG(p, "lst_listisempty", 'c');
            }
        }
    }

}
