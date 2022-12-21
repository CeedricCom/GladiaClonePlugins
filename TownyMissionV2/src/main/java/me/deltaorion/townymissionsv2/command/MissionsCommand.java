package me.deltaorion.townymissionsv2.command;

import me.deltaorion.townymissionsv2.TownyMissionsV2;
import me.deltaorion.townymissionsv2.command.sub.*;
import me.deltaorion.townymissionsv2.player.ContributeType;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MissionsCommand extends BukkitCommand implements TabCompleter {

    public MissionsCommand(TownyMissionsV2 plugin) {
        super(plugin, CommandPermissions.GENERIC.getPerm());
        this.commandArgs = new HashMap<>();
        registerArgs();
    }

    private void registerArgs() {
        commandArgs.put("admin",new MissionAdminCommand(getPlugin()));
        commandArgs.put("dev",new MissionDeveloperCommand());
        commandArgs.put("autocontribute",new AutoContributeCommand(getPlugin()));
        commandArgs.put("contribute",new ContributeCommand());
        commandArgs.put("town",new MissionBearerGUICommand(ContributeType.TOWN));
        commandArgs.put("nation",new MissionBearerGUICommand(ContributeType.NATION));
    }

    private final HashMap<String,SubCommand> commandArgs;

    @Override
    public void onCommand(TownyMissionsV2 plugin, CommandSender sender, ArgumentList args) throws CommandException {

        args.setUsage("/missions help for more help!");

        if(!args.hasArg(0) ||
                args.getArgOrNothing(0).asString().equalsIgnoreCase("help") ||
                args.getArgOrNothing(0).asString().equalsIgnoreCase("?")) {

            printHelpMenu(sender, args);
            return;
        }

        for(Map.Entry<String,SubCommand> entry : commandArgs.entrySet()) {
            if(entry.getKey().equalsIgnoreCase(args.getArgOrFail(0).asString())) {
                args.setUsage(entry.getValue().getUsage());
                entry.getValue().call(plugin,sender,args.reduce());
                return;
            }
        }

        printHelpMenu(sender,args);

    }

    private void printHelpMenu(CommandSender sender, ArgumentList args) {
        final String BASE_COMMAND = "/" + args.getLabel() + " ";
        final String OPENER = ChatColor.GOLD + "" + BASE_COMMAND;

        sender.sendMessage(ChatColor.YELLOW + "---oO " + ChatColor.WHITE + " Towny Missions " + ChatColor.YELLOW + "Oo---" );

        commandArgs.forEach((s, subCommand) -> {
            if (sender.hasPermission(subCommand.getPermission())) {
                sender.sendMessage(OPENER + s + ": " + ChatColor.WHITE + subCommand.getDescription());
            }
        });
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        final List<String> tabCompletions = new ArrayList<>();

        commandArgs.forEach((s, subCommand) -> {
            if(sender.hasPermission(subCommand.getPermission())) {
                if(args.length<=1) {
                    tabCompletions.add(s);
                } else {
                    if (s.equalsIgnoreCase(args[0])) {
                        if(sender.hasPermission(subCommand.getPermission())) {
                            String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
                            tabCompletions.addAll(subCommand.getTabCompletions(sender, newArgs));
                        }
                    }
                }
            }
        });

        return tabCompletions;
    }
}
