package me.cedric.noobprotector.command;

import me.cedric.noobprotector.NoobProtector;
import me.cedric.noobprotector.util.NPUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class PvPAdmin implements CommandExecutor {

    NoobProtector plg;
    NPUtil u;

    public PvPAdmin(final NoobProtector plg) {
        this.plg = plg;
        this.u = plg.u;
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {

        if (!(sender instanceof Player)) {
            return true;
        }

        Player p = (Player) sender;

        if (!p.hasPermission("noobprotector.admin")) {
            p.sendMessage(ChatColor.RED + "No permission.");
            return true;
        }

        if (args.length > 0 && this.u.checkCmdPerm(p, args[0])) {
            if (args.length == 1) {
                return this.ExecuteCmd(p, args[0]);
            }
            if (args.length == 2) {
                return this.ExecuteCmd(p, args[0], args[1]);
            }
            if (args.length == 3) {
                return this.ExecuteCmd(p, args[0], args[1], args[2]);
            }
        } else {
            this.u.PrintHlpList(p, 1, 15);
        }
        return true;
    }

    private boolean ExecuteCmd(Player p, final String cmd) {
        if (cmd.equalsIgnoreCase("help")) {
            this.u.PrintHlpList(p, 1, 15);
            this.u.printMSG(p, "msg_pvponcmd", "/pvp-on");
        }
        else if (cmd.equalsIgnoreCase("protect")) {
            this.plg.players.setPlayer(p);
            this.plg.players.printPlayerProtected(p, false, true);
        } else if (cmd.equalsIgnoreCase("unprotect")) {
            if (this.plg.players.unprotectPlayer(p)) {
                this.u.printMSG(p, "msg_youunprotected");
            } else {
                this.u.printMSG(p, "msg_unprtfail", p.getName());
            }
        } else if (cmd.equalsIgnoreCase("list")) {
            this.plg.players.printList(p, 1, "");
        } else if (cmd.equalsIgnoreCase("reload")) {
            this.plg.reloadConfig();
            this.plg.loadCfg();
            this.u.printMSG(p, "msg_reloaded");
        } else {
            if (!cmd.equalsIgnoreCase("cfg")) {
                return false;
            }
            this.u.PrintCfg(p);
        }
        return true;
    }

    private boolean ExecuteCmd(Player p, final String cmd, final String arg) {
        if (cmd.equalsIgnoreCase("help")) {
            int page = 1;
            if (this.u.isInteger(arg)) {
                page = Integer.parseInt(arg);
            }
            this.u.PrintHlpList(p, page, 15);
        }
        else if (cmd.equalsIgnoreCase("protect")) {
            final Player prp = this.u.getPlayerByName(arg);
            if (prp != null && prp.isOnline()) {
                this.plg.players.setPlayer(prp);
                this.plg.players.printPlayerProtected(prp, false, true);
                this.plg.players.printTargetPlayerProtected(p, prp);
            } else {
                this.u.printMSG(p, "msg_unknownplayer", arg);
            }
        }
        else if (cmd.equalsIgnoreCase("unprotect")) {
            final Player prp = this.u.getPlayerByName(arg);
            if (this.plg.players.unprotectPlayer(arg)) {
                if (prp != null && prp.isOnline()) {
                    this.u.printMSG(prp, "msg_youunprotected");
                }
                this.u.printMSG(p, "msg_plrisunprotected", arg);
            } else {
                this.u.printMSG(p, "msg_unprtfail", arg);
            }
        }
        else {
            if (!cmd.equalsIgnoreCase("list")) {
                return false;
            }
            int pnum = 1;
            String mask = "";
            if (arg.matches("[1-9]+[0-9]*")) {
                pnum = Integer.parseInt(arg);
            } else {
                mask = arg;
            }
            this.plg.players.printList(p, pnum, mask);
        }
        return true;
    }

    private boolean ExecuteCmd(Player p, final String cmd, final String arg1, final String arg2) {
        if (cmd.equalsIgnoreCase("list")) {
            int pnum = 1;
            String mask = "";
            if (arg1.matches("[1-9]+[0-9]*")) {
                pnum = Integer.parseInt(arg1);
                mask = arg2;
            }
            else if (arg2.matches("[1-9]+[0-9]*")) {
                pnum = Integer.parseInt(arg2);
                mask = arg1;
            }
            this.plg.players.printList(p, pnum, mask);
            return true;
        }
        return false;
    }

}
