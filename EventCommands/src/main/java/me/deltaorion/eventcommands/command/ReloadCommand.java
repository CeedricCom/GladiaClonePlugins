package me.deltaorion.eventcommands.command;

import me.deltaorion.common.command.CommandException;
import me.deltaorion.common.command.FunctionalCommand;
import me.deltaorion.common.command.sent.SentCommand;
import me.deltaorion.eventcommands.EventCommandsPlugin;

public class ReloadCommand extends FunctionalCommand {

    private final EventCommandsPlugin plugin;

    protected ReloadCommand(EventCommandsPlugin plugin) {
        super("EventCommands.Command.Reload");
        this.plugin = plugin;
    }

    @Override
    public void commandLogic(SentCommand command) throws CommandException {
        plugin.loadConfig();
        command.getSender().sendMessage("Successfully reloaded config");
    }
}
