package me.deltaorion.townymissionsv2.command;

import me.deltaorion.townymissionsv2.TownyMissionsV2;
import me.deltaorion.townymissionsv2.configuration.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public abstract class BukkitCommand implements CommandExecutor {

    private final TownyMissionsV2 plugin;
    private final String permission;

    protected BukkitCommand(TownyMissionsV2 plugin, String permission) {
        this.plugin = plugin;
        this.permission = permission;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(!sender.hasPermission(permission)) {
            sender.sendMessage(Message.NO_PERMISSION.getMessage());
            return true;
        }

        try {
            this.onCommand(plugin,sender,new ArgumentList(plugin,args,label));
        } catch (CommandException e) {
            sender.sendMessage(e.getMessage());
        }
        return true;
    }

    protected TownyMissionsV2 getPlugin() {
        return plugin;
    }

    public abstract void onCommand(TownyMissionsV2 plugin, CommandSender sender, ArgumentList args) throws CommandException;
}
