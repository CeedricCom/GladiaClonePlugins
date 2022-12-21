package me.deltaorion.townymissionsv2.command;

import me.deltaorion.townymissionsv2.TownyMissionsV2;
import me.deltaorion.townymissionsv2.configuration.Message;
import org.bukkit.command.CommandSender;

import java.util.List;

public interface SubCommand {

    default void call(TownyMissionsV2 plugin, CommandSender sender, ArgumentList args) throws CommandException {
        if(!sender.hasPermission(getPermission())) {
            sender.sendMessage(Message.NO_PERMISSION.getMessage());
        } else {
            onCommand(plugin,sender,args);
        }
    }

    public void onCommand(TownyMissionsV2 plugin, CommandSender sender, ArgumentList args) throws CommandException;

    public String getDescription();

    public String getPermission();

    public String getUsage();

    public List<String> getTabCompletions(CommandSender sender, String[] args);
}
