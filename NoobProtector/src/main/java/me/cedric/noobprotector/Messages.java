package me.cedric.noobprotector;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Messages {

    static NoobProtector noobProtector = NoobProtector.i;
    static YamlConfiguration yaml = new YamlConfiguration();
    private static final String path = noobProtector.getDataFolder().getPath() + File.separator + "messages.yml";

    public static void initialize() {
        try {

            File file = new File(path);
            if (!file.exists()) {
                if (file.createNewFile()) {
                    createMessages();
                } else {
                    System.out.println("[NoobProtector] Could not create file.");
                    return;
                }

            }

            yaml.load(path);

        } catch (IOException | InvalidConfigurationException ignored) {}
    }

    private static void createMessages() {
        try {
            yaml.load(path);

            yaml.set("msg_warnpvpon", "Warning! PVP-protection removed. You can now attack and be attacked by other players!");
            yaml.set("msg_plisttitle", "Noob-protected players");
            yaml.set("msg_plistfooter", "Page: [%1% / %2%]");
            yaml.set("msg_emptylist", "There's no Noob-protected players");
            yaml.set("msg_reloaded", "configuration reloaded from file");
            yaml.set("msg_pvponcmd", "%1% - to disable your own protection");
            yaml.set("msg_youunprotected", "Your PVP-protection was removed");
            yaml.set("msg_unprtfail", "Failed to remove %1%'s protection");
            yaml.set("msg_plrisprotected", "%1% is now protected");
            yaml.set("msg_plrisunprotected", "%1% is unprotected");
            yaml.set("msg_unknownplayer", "Player %1% is unknown. May be he is offline?");
            yaml.set("msg_unprtfail", "Failed to remove protection from %1%");
            yaml.set("msg_alreadyunprotected", "You are already unprotected!");
            yaml.set("msg_pvponcooldown", "Type %1% again in the next %2% seconds to unprotect yourself");
            yaml.set("msg_currenttime", "Current (server) time is %1%");
            yaml.set("msg_warnpvpoff", "You are now protected from PVP-attacks. But you cannot attack other players too. PVP-protection will be removed at %1%");
            yaml.set("msg_youcantattack", "Hey! You cannot attack other players! Type %1% to remove protection.");
            yaml.set("msg_warndefender", "%1% is trying to attack you!");
            yaml.set("msg_warnatacker", "%1% is PVP-protected. Your attack failed.");
            yaml.set("msg_protected", "You are protected.");
            yaml.set("msg_notprotected", "You are unprotected.");
            yaml.set("msg_playtime", "Play-time (online) limit: %1%");
            yaml.set("msg_realtime", "Unprotection time: %1%");
            yaml.set("msg_typepvpon", "Type %1% to remove protection.");
            yaml.set("msg_servertimezone", "Default (system) time zone: %1%");
            yaml.set("msg_cfgtimezone", "Time zone modifier (defined in config-file): %1%");
            yaml.set("msg_currenttime", "Time in defined time zone is %1%");
            yaml.set("msg_joinprotect", "Auto protect new players");
            yaml.set("msg_playerwarn", "Warn player about protection");
            yaml.set("msg_playerwarntime", "Warning message delay: %1% minutes");
            yaml.set("msg_useplaytime", "Use play-time (online) protection");
            yaml.set("msg_useplaytimevalue", "Play-time protection limit %1% minutes");
            yaml.set("msg_userealtime", "Use real-time protection");
            yaml.set("msg_config", "Configuration");
            yaml.set("msg_userealtimevalue", "Real-time protection limit %1% minutes");
            yaml.set("msg_pvponcooldowntime", "/pvp-on command delay: %1% seconds");
            yaml.set("msg_pvpupdatetime", "Update protection status every %1% minutes");
            yaml.set("msg_language", "Language: %1%");
            yaml.set("msg_versioncheck", "Check plugin updates");
            yaml.set("msg_outdated", "%1% is outdated!");
            yaml.set("msg_pleasedownload", "Please download new version (%1%) from ");

            yaml.set("cfg_configuration", "Configuration");

            yaml.set("cmd_protect", "%1% - protect player (or protect yourself)");
            yaml.set("cmd_unprotect", "%1% - unprotect player (or unprotect yourself)");
            yaml.set("cmd_list", "%1% - list all protected players");
            yaml.set("cmd_reload", "%1% - reload configuration from file");
            yaml.set("cmd_cfg", "%1% - show plugin configuration");
            yaml.set("cmd_unknown", "Unknown command: %1%");
            yaml.set("cmd_cmdpermerr", "Something wrong (check command, permissions)");

            yaml.set("hlp_help", "Help");
            yaml.set("hlp_thishelp", "%1% - this help");
            yaml.set("hlp_execcmd", "%1% - execute command");
            yaml.set("hlp_typecmd", "Type %1% - to get additional help");
            yaml.set("hlp_typecmdpage", "Type %1% - to see another page of this help");
            yaml.set("hlp_commands", "Command list:");
            yaml.set("hlp_cmdparam_command", "command");
            yaml.set("hlp_cmdparam_page", "page");
            yaml.set("hlp_cmdparam_parameter", "parameter");

            yaml.set("lst_title", "String list:");
            yaml.set("lst_footer", "Page: [%1% / %2%]");
            yaml.set("lst_listisempty", "List is empty");



            yaml.set("cfgmsg_general_check-updates", "Check updates: %1%");
            yaml.set("cfgmsg_general_language", "Language: %1%");
            yaml.set("cfgmsg_general_language-save", "Save translation file: %1%");

            yaml.set("disabled", "disabled");

            yaml.save(path);
        } catch (IOException | InvalidConfigurationException ignored) {}
    }

    private static String getMessage(String root) {
        return yaml.getString(root);
    }

    public static boolean isMessage(String message) {
        return yaml.contains(message);
    }

    public static String getMSG(final Object... s) {
        String str = "&4Unknown message";
        if (s.length > 0) {
            final String id = s[0].toString();
            str = "&4Unknown message (" + id + ")";
            if (isMessage(id)) {
                int px = 1;
                if (s.length > 1 && s[1] instanceof Character) {
                    px = 2;
                    if (s.length > 2 && s[2] instanceof Character) {
                        px = 3;
                    }
                }
                str = getMessage(id);
                if (px < s.length) {
                    for (int i = px; i < s.length; ++i) {
                        String f = s[i].toString();
                        if (s[i] instanceof Location) {
                            final Location loc = (Location) s[i];
                            f = loc.getWorld().getName() + "[" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + "]";
                        }
                        str = str.replace("%" + (i - px + 1) + "%", f);
                    }
                }
            }
        }
        return ChatColor.translateAlternateColorCodes('&', str);
    }


}
