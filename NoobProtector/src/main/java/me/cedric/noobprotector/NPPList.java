package me.cedric.noobprotector;

import java.util.List;
import java.util.ArrayList;

import me.cedric.noobprotector.util.NPUtil;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.bukkit.scheduler.BukkitTask;

public class NPPList {

    NoobProtector plg;
    Long prtreal;
    Long prtplay;
    NPUtil u;
    BukkitTask tid;
    BukkitTask tid2;
    boolean userealtime;
    boolean useplaytime;
    private final HashMap<String, NPPlayer> players;

    public NPPList(final NoobProtector plg) {
        this.prtreal = 3600000L;
        this.prtplay = 5L;
        this.userealtime = true;
        this.useplaytime = true;
        this.players = new HashMap<>();
        this.plg = plg;
        this.u = plg.u;
        this.prtreal = plg.prttime * 60000L;
        this.prtplay = plg.prtplay * 60000L;
        this.useplaytime = plg.useplaytime;
        this.userealtime = plg.userealtime;
        this.loadPlayerList();
        this.tid = Bukkit.getScheduler().runTaskTimerAsynchronously(this.plg, NPPList.this::updateOnlinePlayersPVP,
                200L, plg.pvpupdatetime * 20L);
        if (plg.playerwarn) {
            this.tid2 = Bukkit.getScheduler().runTaskTimerAsynchronously(this.plg, NPPList.this::warnPlayers,
                    plg.playerwarntime * 1200L, plg.playerwarntime * 1200L);
        }
    }

    public void warnPlayers() {
        for (final Player p : Bukkit.getOnlinePlayers()) {
            this.printPlayerProtected(p, false, true);
        }
    }

    public boolean updatePlayTime(final Player p) {
        if (!this.players.containsKey(p.getName())) {
            return false;
        }
        Long pt;
        final Long ct = pt = System.currentTimeMillis();
        if (p.hasMetadata("NP-checktime")) {
            pt = p.getMetadata("NP-checktime").get(0).asLong();
        }
        p.setMetadata("NP-checktime", new FixedMetadataValue(this.plg, ct));
        final Long playtime = ct - pt;
        return this.players.get(p.getName()).updatePlayTime(playtime);
    }

    public void addPlayer(final Player p) {
        if (!this.players.containsKey(p.getName())) {
            this.players.put(p.getName(), new NPPlayer());
        }
        this.savePlayerList();
    }

    public void setPlayer(final Player p) {
        this.players.put(p.getName(), new NPPlayer());
        this.savePlayerList();
    }

    public void setPlayer(final String pname) {
        this.players.put(pname, new NPPlayer());
        this.savePlayerList();
    }

    public boolean unprotectPlayer(final Player p) {
        if (this.players.containsKey(p.getName())) {
            this.players.remove(p.getName());
            this.savePlayerList();
            return true;
        }
        return false;
    }

    public boolean unprotectPlayer(final String pname) {
        if (this.players.containsKey(pname)) {
            this.players.remove(pname);
            this.savePlayerList();
            return true;
        }
        return false;
    }

    public void updateOnlinePlayersPVP() {
        for (final Player p : Bukkit.getOnlinePlayers()) {
            if (this.updatePlayerPVP(p)) {
                this.u.printMSG(p, "msg_warnpvpon", '6');
            }
        }
    }

    public boolean updatePlayerPVP(final Player p) {
        if (this.players.containsKey(p.getName()) && ((this.userealtime && this.players.get(p.getName()).rtimelimit < System.currentTimeMillis()) || (this.useplaytime && !this.updatePlayTime(p)))) {
            this.players.remove(p.getName());
            return true;
        }
        return false;
    }

    public boolean getPvpOff(final Player p) {
        return !this.isPlayerInUnprotectedWorld(p) && this.players.containsKey(p.getName()) &&
                ((this.userealtime && this.players.get(p.getName()).rtimelimit > System.currentTimeMillis()) ||
                        (this.useplaytime && this.updatePlayTime(p)));
    }

    public boolean isPlayerInUnprotectedWorld(final Player p) {
        return !this.plg.no_pvp_worlds.isEmpty() && this.plg.no_pvp_worlds.contains(p.getWorld().getName());
    }

    public void savePlayerList() {
        try {
            final File f = new File(this.plg.getDataFolder() + File.separator + "players.yml");
            if (this.players.size() > 0) {
                f.createNewFile();
                final YamlConfiguration cfg = new YamlConfiguration();
                for (final String name : this.players.keySet()) {
                    if (this.useplaytime) {
                        cfg.set(name + ".playtime", this.players.get(name).playtimeleft);
                    }
                    if (this.userealtime) {
                        cfg.set(name + ".realtime", this.players.get(name).rtimelimit);
                    }
                }
                cfg.save(f);
            }
        } catch (Exception ignored) {}
    }

    public void loadPlayerList() {
        try {
            final File f = new File(this.plg.getDataFolder() + File.separator + "players.yml");
            if (f.exists()) {
                final YamlConfiguration cfg = new YamlConfiguration();
                cfg.load(f);
                for (final String name : cfg.getKeys(false)) {
                    this.players.put(name, new NPPlayer(cfg.getLong(name + ".realtime", 0L),
                            cfg.getLong(name + ".playtime", 0L)));
                }
            }
        }
        catch (Exception ex) {}
    }

    public String getProtectTime(final Player p) {
        return this.getProtectTime(p.getName());
    }

    private String formatLongList(List<Long> longs) {

        if (longs.size() != 4) return null;
        List<String> time = new ArrayList<>();
        if (longs.get(3) != 0) {
            time.add(longs.get(3) + " days");
        }
        if (longs.get(2) != 0) {
            time.add(longs.get(2) + " hours");
        }
        if (longs.get(1) != 0) {
            time.add(longs.get(1) + " minutes");
        }
        if (longs.get(0) != 0) {
            time.add(longs.get(0) + " seconds");
        }

        return time.toString().replace("]", "").replace("[", "");
    }

    public String getProtectTime(final String pname) {
        if (this.players.containsKey(pname)) {
            long time = this.players.get(pname).rtimelimit - System.currentTimeMillis();

            long seconds = TimeUnit.MILLISECONDS.toSeconds(time);
            long minutes = TimeUnit.SECONDS.toMinutes(seconds);
            long hours = TimeUnit.MINUTES.toHours(minutes);
            long days = TimeUnit.HOURS.toDays(hours);
            int toSubtract = (int) hours * 60;
            int subtractBySeconds = (int) minutes * 60;
            int hoursToSubtract = (int) days * 24;

            List<Long> longs = new ArrayList<>();
            longs.add(seconds - subtractBySeconds); // 0 (seconds)
            longs.add(minutes - toSubtract); // 1 (minutes)
            longs.add(hours - hoursToSubtract); // 2 (hours)
            longs.add(days); // days
            int i = 0;
            for (long b : longs) {
                if (b <= 0) {
                    longs.set(i, (long) 0);
                }
                i++;
            }

            return formatLongList(longs);
        }
        return "";
    }

    public String getPlayTimeLeft(final Player p) {
        return this.getPlayTimeLeft(p.getName());
    }

    public String getPlayTimeLeft(final String pname) {
        if (this.players.containsKey(pname)) {
            final long time = this.players.get(pname).playtimeleft / 1000L;
            final int seconds = (int)(time % 60L);
            final int minutes = (int)(time % 3600L / 60L);
            final int hours = (int)(time / 3600L);
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }
        return "";
    }

    public void printPlayerProtected(Player p, final boolean prtempty, final boolean pvpon) {
        if (this.players.containsKey(p.getName())) {
            String msg = Messages.getMSG("msg_protected");
            if (this.useplaytime) {
                msg = msg + " " + Messages.getMSG("msg_playtime", this.getPlayTimeLeft(p));
            }
            if (this.userealtime) {
                msg = msg + " " + Messages.getMSG("msg_realtime", this.getProtectTime(p));
            }
            this.u.printMsg(p, msg);
            if (pvpon) {
                this.u.printMSG(p, "msg_typepvpon", "/pvp-on");
            }
        } else if (prtempty) {
            this.u.printMSG(p, "msg_notprotected");
        }
    }

    public void printTargetPlayerProtected(Player player, final Player tp) {
        if (this.players.containsKey(tp.getName())) {
            String msg = Messages.getMSG("msg_plrisunprotected", tp.getName());
            if (this.useplaytime) {
                msg = msg + " " + Messages.getMSG("msg_playtime", this.getPlayTimeLeft(tp));
            }
            if (this.userealtime) {
                msg = msg + " " + Messages.getMSG("msg_realtime", this.getProtectTime(tp));
            }
            this.u.printMsg(player, msg);
        }
        else {
            this.u.printMSG(player, "msg_plrisunprotected", tp.getName());
        }
    }

    public void printList(Player player, final int pnum, final String mask) {
        if (this.players.size() > 0) {
            final List<String> ln = new ArrayList<>();
            for (final String name : this.players.keySet()) {
                String pt = "";
                if (this.userealtime) {
                    pt = pt + " : " + this.getProtectTime(name);
                }
                if (this.useplaytime) {
                    pt = pt + " : " + this.getPlayTimeLeft(name);
                }
                if (mask.isEmpty() || name.contains(mask)) {
                    ln.add("&2" + name + " &a" + pt);
                }
            }
            if (ln.size() > 0) {
                this.u.printPage(player, ln, pnum, "msg_plisttitle", "msg_plistfooter", true);
            }
            else {
                this.u.printMSG(player, "msg_emptylist");
            }
        }
        else {
            this.u.printMSG(player, "msg_emptylist");
        }
    }

    public class NPPlayer {
        long rtimelimit;
        long playtimeleft;

        public NPPlayer() {
            this.rtimelimit = System.currentTimeMillis() + NPPList.this.prtreal;
            this.playtimeleft = NPPList.this.prtplay;
        }

        public NPPlayer(final Long rtimelimit, final Long playtimeleft) {
            this.rtimelimit = rtimelimit;
            this.playtimeleft = playtimeleft;
        }

        public boolean updatePlayTime(final Long pt) {
            this.playtimeleft = Math.max(this.playtimeleft - pt, 0L);
            return this.playtimeleft > 0L;
        }
    }

}
