package me.deltaorion.townymissionsv2.command.admin;

import me.deltaorion.townymissionsv2.TownyMissionsV2;
import me.deltaorion.townymissionsv2.command.ArgumentList;
import me.deltaorion.townymissionsv2.command.CommandException;
import me.deltaorion.townymissionsv2.command.CommandPermissions;
import me.deltaorion.townymissionsv2.configuration.Message;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class MissionAdminReloadCommand implements me.deltaorion.townymissionsv2.command.SubCommand {
    @Override
    public void onCommand(TownyMissionsV2 plugin, CommandSender sender, ArgumentList args) throws CommandException {
        switch (args.getArgOrFail(0).asString()) {
            case "sounds":
                plugin.reloadSounds();
                sender.sendMessage("Successfully Reloaded Sounds");
                break;
            case "messages":
                plugin.reloadMessages();
                sender.sendMessage("Successfully Reloaded Messages");
                break;
            case "pool":
                plugin.reloadPool();
                sender.sendMessage("Successfully Reloaded Mission Pool");
                break;
            case "all":
                plugin.reloadMessages();
                plugin.reloadSounds();
                plugin.reloadPool();
                plugin.loadConfig();
                sender.sendMessage("Successfully Reloaded All Configs");
                break;
            case "config":
                plugin.reloadConfig();
                sender.sendMessage("Successfully Reloaded Generic Config");
                break;
            default:
                throw new CommandException(Message.COMMAND_BAD_USAGE.getMessage(getUsage()));
        }
    }

    @Override
    public String getDescription() {
        return "Reloads the selected configuration";
    }

    @Override
    public String getPermission() {
        return CommandPermissions.ADMIN_RELOAD.getPerm();
    }

    @Override
    public String getUsage() {
        return "/missions admin reload [sounds/messages/pool/config/all]";
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();
        completions.add("sounds");
        completions.add("messages");
        completions.add("pool");
        completions.add("config");
        completions.add("all");
        return completions;
    }
}
