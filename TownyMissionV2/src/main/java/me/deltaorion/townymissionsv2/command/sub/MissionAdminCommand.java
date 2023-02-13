package me.deltaorion.townymissionsv2.command.sub;

import com.palmergames.bukkit.towny.object.Government;
import me.deltaorion.townymissionsv2.TownyMissionsV2;
import me.deltaorion.townymissionsv2.command.*;
import me.deltaorion.townymissionsv2.command.admin.*;
import me.deltaorion.townymissionsv2.configuration.Message;
import me.deltaorion.townymissionsv2.util.TownyUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.*;

public class MissionAdminCommand implements SubCommand {

    private final TownyMissionsV2 plugin;

    public MissionAdminCommand(TownyMissionsV2 plugin) {
        this.commandArgs = new HashMap<>();
        this.plugin = plugin;
        registerArgs();
    }

    private void registerArgs() {
        //reload
        //give-mission
        //remove-mission
        //complete-mission
        //complete-current-goal
        //set-stage
        //add-cooldown
        //remove-cooldown
        commandArgs.put("reload",new MissionAdminReloadCommand());
        commandArgs.put("give-mission",new MissionAdminGiveCommand(plugin));
        commandArgs.put("remove-mission",new MissionAdminRemoveCommand());
        commandArgs.put("complete-mission",new MissionAdminCompleteCommand());
        commandArgs.put("complete-current-goal",new MissionAdminCompleteStage());
        commandArgs.put("set-stage",new MissionAdminSetStage());
        commandArgs.put("add-cooldown",new MissionAdminCooldownAdd());
        commandArgs.put("remove-cooldown",new MissionAdminCooldownRemove());
        commandArgs.put("tick",new MissionsTickCommand());
        commandArgs.put("save-database",new MissionAdminSaveDatabase());
        commandArgs.put("docs",new DocumentationCommand());
        commandArgs.put("multiplier",new ContributionMultiplierCommand());
    }

    private final HashMap<String,SubCommand> commandArgs;

    @Override
    public void onCommand(TownyMissionsV2 plugin, CommandSender sender, ArgumentList args) throws CommandException {

        args.setUsage("/missions help");

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

    @Override
    public String getDescription() {
        return "Commands used for moderation, administration and testing";
    }

    @Override
    public String getPermission() {
        return CommandPermissions.ADMIN.getPerm();
    }

    @Override
    public String getUsage() {
        return "/missions admin for more info";
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
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

    private void printHelpMenu(CommandSender sender, ArgumentList args) {
        final String BASE_COMMAND = "/" + args.getLabel() + " admin ";
        final String OPENER = ChatColor.GOLD + "" + BASE_COMMAND;

        sender.sendMessage(ChatColor.YELLOW + "---oO " + ChatColor.WHITE + " Towny Missions " + ChatColor.YELLOW + "Oo---" );

        commandArgs.forEach((s, subCommand) -> {
            if (sender.hasPermission(subCommand.getPermission())) {
                sender.sendMessage(OPENER + s + ": " + ChatColor.WHITE + subCommand.getDescription());
            }
        });
    }

    public final static CommandParser GOV_PARSER = new CommandParser() {
        @Override
        public <T> T parse(String str) throws CommandException {
            Government government = TownyUtil.getGovernment(str);
            if(government==null)
                throw new CommandException(Message.COMMAND_NOT_GOVERNEMENT.getMessage(str));

            return (T) government;
        }
    };

}
