package me.deltaorion.siegecommandblacklist;

import com.gmail.goosius.siegewar.utils.SiegeWarDistanceUtil;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.regex.Pattern;

public class CommandListener implements org.bukkit.event.Listener {

    private final static Pattern WHITESPACE = Pattern.compile("\\s+");
    private final BlackListConfig blackListConfig;

    public CommandListener(BlackListConfig blackListConfig) {
        this.blackListConfig = blackListConfig;
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if(e.getPlayer().hasPermission(Permissions.BYPASS))
            return;

        String message = e.getMessage();
        if(SiegeWarDistanceUtil.isLocationInActiveSiegeZone(e.getPlayer().getLocation())) {
            if(message.startsWith("/"))
                message = message.substring(1,message.length());

            String[] args = WHITESPACE.split(message);
            if(args.length<=0)
                return;

            for(String command : blackListConfig.getBlackList()) {
                if(command.equalsIgnoreCase(args[0])) {
                    e.getPlayer().sendMessage(ChatColor.RED + "You may not use the command '"+command+"' in a siege zone");
                    e.setCancelled(true);
                    return;
                }
            }
        }
    }
}
