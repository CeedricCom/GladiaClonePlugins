package me.deltaorion.siegestats.controller.command;

import me.deltaorion.common.command.Command;
import me.deltaorion.common.command.CommandException;
import me.deltaorion.common.command.FunctionalCommand;
import me.deltaorion.common.command.sent.SentCommand;
import me.deltaorion.siegestats.Permissions;
import me.deltaorion.siegestats.SiegeStatsPlugin;

import java.util.Map;

public class SiegeStatCommand extends FunctionalCommand {

    private final SiegeStatsPlugin plugin;

    public SiegeStatCommand(SiegeStatsPlugin plugin) {
        super(Permissions.COMMAND);
        this.plugin = plugin;
        registerArgument("view",new ViewCommand(plugin.getSiegeManager()));
        registerArgument("list",new ListCommand(plugin.getSiegeManager()));
        registerArgument("participant",new ParticipantCommand(this.plugin.getSiegeManager()));
        registerArgument("logs",new LogsCommand(this.plugin.getSiegeManager()));
        registerArgument("database",new SaveCommand(plugin.getPersistenceManager()));
        registerArgument("report",new ReportCommand(plugin, plugin.getReportDir(),plugin.getSiegeManager()));
    }

    @Override
    public void commandLogic(SentCommand command) throws CommandException {
        for(Map.Entry<String, Command>  commandFunction : getFunctions().entrySet()) {
            command.getSender().sendMessage("/"+command.getLabel()+" "+commandFunction.getKey()+": "+commandFunction.getValue().getDescription());
        }
    }
}
