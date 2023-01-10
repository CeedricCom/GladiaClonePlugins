package me.deltaorion.siegecommandblacklist;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class BlacklistCommand implements CommandExecutor {

    private final SiegeCommandBlacklist plugin;

    public BlacklistCommand(SiegeCommandBlacklist plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission(Permissions.COMMAND)) {
            sender.sendMessage(ChatColor.RED + "Hey! You do not have permission to use this command");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("/siegecommandblacklist [info,reload]");
            return true;
        }

        if (args[0].equalsIgnoreCase("info")) {
            sender.sendMessage("Blacklisted command");
            for (String cmd : plugin.getBlackListConfig().getBlackList()) {
                sender.sendMessage("- " + cmd);
            }
            return true;
        }

        if(args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage("Reloading Configuration");
            plugin.getBlackListConfig().reload();
            return true;
        }

        sender.sendMessage("/siegecommandblacklist [info,reload]");
        return true;
    }
}
